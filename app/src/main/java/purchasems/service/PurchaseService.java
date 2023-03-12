package purchasems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import purchasems.api.model.adapter.AdapterResponse;
import purchasems.api.model.customer.CustomerResponse;
import purchasems.api.model.order.OrderRequest;
import purchasems.api.model.orderline.OrderLineRequest;
import purchasems.api.model.product.ProductSupplier;
import purchasems.api.model.purchase.PurchaseResponse;
import purchasems.api.model.purchase.PurchaseStatus;
import purchasems.api.model.supplier.SupplierResponse;
import purchasems.configuration.AdaptersMap;
import purchasems.exception.NotExistException;
import purchasems.mapper.OrderLineMapper;
import purchasems.mapper.PurchaseMapper;
import purchasems.persistence.domain.OrderLineEntity;
import purchasems.persistence.domain.PurchaseEntity;
import purchasems.persistence.repository.OrderLineRepository;
import purchasems.persistence.repository.PurchaseRepository;
import purchasems.util.JavaxValidator;

/** Purchase services. */
@AllArgsConstructor
@Service
@Slf4j
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final OrderLineRepository orderLineRepository;
  private final PurchaseMapper purchaseMapper;
  private final OrderLineMapper orderLineMapper;
  private final RestTemplate restTemplate;
  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper mapper;
  private final AdaptersMap adaptersMap;

  /**
   * Get a purchase by purchase id.
   *
   * @param purchaseId Purchase id.
   * @return Purchase Response.
   * @throws NotExistException Not Exist Exception.
   */
  public PurchaseResponse getPurchase(String purchaseId) throws NotExistException {
    PurchaseEntity purchase = purchaseRepository.findPurchaseEntityByPurchaseId(purchaseId);
    if (purchase == null) {
      log.info("Purchase Not Found {}", purchaseId);
      throw new NotExistException("Customer");
    } else {
      log.info("Found Purchase: {}", purchaseId);
    }
    return purchaseMapper.fromEntity(purchase);
  }

  /**
   * Get all purchases with filters.
   *
   * @param orderId Order id filter.
   * @param sort Sort by.
   * @param limit Limit pagination.
   * @param offset Offset pagination.
   * @return List of Purchase Responses.
   * @throws NotExistException Not Exist Exception.
   */
  public List<PurchaseResponse> getPurchases(String orderId, String sort, int limit, int offset)
      throws NotExistException {
    String[] sortArray = sort.split("\\.");
    Sort.Direction direction =
        sortArray[0].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable =
        PageRequest.of(offset, limit, Sort.by(new Sort.Order(direction, sortArray[1])));

    List<PurchaseEntity> purchases = purchaseRepository.findByOrderIdPagination(orderId, pageable);
    if (purchases.isEmpty()) {
      log.info("Purchases Not Found for order: {}", orderId);
      throw new NotExistException("Purchase");
    }
    log.info("Found Products for order: {}", orderId);
    List<PurchaseResponse> response = new ArrayList<>();
    for (PurchaseEntity purchase : purchases) {
      response.add(purchaseMapper.fromEntity(purchase));
    }
    return response;
  }

  /**
   * Split order into purchase entities.
   *
   * @param order Order Request.
   * @param productSuppliers Product Suppliers array.
   * @param correlation Correlation id.
   * @throws JsonProcessingException Json Processing Exception.
   */
  public void splitAndSend(
      OrderRequest order, List<ProductSupplier> productSuppliers, String correlation)
      throws JsonProcessingException {
    List<SupplierResponse> uniqueSuppliers = new ArrayList<>();
    List<OrderLineRequest> items = order.getItems();

    sortLists(uniqueSuppliers, items, productSuppliers);
    List<PurchaseEntity> purchases =
        splitPurchases(uniqueSuppliers, items, productSuppliers, order.getOrderId());
    sendToAdapters(purchases, correlation);
  }

  /**
   * Utility method to sort needed lists.
   *
   * @param uniqueSuppliers List of unique suppliers.
   * @param items List of orderLine requests.
   * @param productSuppliers List of ProductSuppliers.
   */
  protected void sortLists(
      List<SupplierResponse> uniqueSuppliers,
      List<OrderLineRequest> items,
      List<ProductSupplier> productSuppliers) {
    // Remove duplicated suppliers
    for (ProductSupplier productSupplier : productSuppliers) {
      if (!uniqueSuppliers.contains(productSupplier.getSupplier())) {
        uniqueSuppliers.add(productSupplier.getSupplier());
      }
    }

    // Sorting lists
    uniqueSuppliers.sort(Comparator.comparing(SupplierResponse::getId));
    items.sort(Comparator.comparing(a -> a.getProduct().getId()));
    productSuppliers.sort(Comparator.comparing(ProductSupplier::getId));
  }

  List<PurchaseEntity> splitPurchases(
      List<SupplierResponse> uniqueSuppliers,
      List<OrderLineRequest> items,
      List<ProductSupplier> productSuppliers,
      String orderId) {
    List<PurchaseEntity> purchases = new ArrayList<>();

    // One purchase for each supplier
    for (SupplierResponse supplier : uniqueSuppliers) {
      PurchaseEntity purchase = new PurchaseEntity();
      purchase.setStatus(PurchaseStatus.PROCESSING);
      purchase.setOrderId(orderId);
      purchase.setSupplierId(supplier.getId());
      purchase.setSupplierName(supplier.getName());
      purchase.setDatetime(new Timestamp(System.currentTimeMillis()));
      purchase = purchaseRepository.save(purchase);

      // One orderLine for each product linked to this supplier
      for (int i = 0; i < productSuppliers.size(); i++) {
        if (productSuppliers.get(i).getSupplier().getId().equals(supplier.getId())) {
          OrderLineEntity entity = new OrderLineEntity();

          // After sorting both arrays I am sure that this is the correct quantity for this product
          orderLineMapper.toEntity(productSuppliers.get(i), items.get(i).getQuantity(), entity);

          purchase.addOrderLine(entity);
          orderLineRepository.save(entity);
        }
      }
      purchases.add(purchase);
    }
    return purchases;
  }

  /**
   * Send the purchase request to the correct adapters.
   *
   * @param purchases List of Purchases
   * @param correlation Correlation id.
   * @throws JsonProcessingException Json Processing Exception while parsing Purchase.
   */
  void sendToAdapters(List<PurchaseEntity> purchases, String correlation)
      throws JsonProcessingException {
    List<PurchaseResponse> purchaseResponse = new ArrayList<>();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Correlation-Id", correlation);
    // Parses into PurchaseResponse
    for (PurchaseEntity purchase : purchases) {
      purchaseResponse.add(purchaseMapper.fromEntity(purchase));
    }
    for (PurchaseResponse purchase : purchaseResponse) {
      HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(purchase), headers);
      try {
        ResponseEntity<String> response =
            restTemplate.postForEntity(
                adaptersMap.getEndpoints().get(purchase.getSupplier().getId()),
                request,
                String.class);
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
          log.error("Request for Supplier: {} Failed!", purchase.getSupplier().getId());
        } else {
          log.info("Request sent to Supplier: {}", purchase.getSupplier().getId());
        }
      } catch (Exception e) {
        // If this adapter/supplier is down the request is sent but fails to complete the purchase.
        // In that case IDK what is the best action to take since a redelivery could cause the other
        // purchases to be duplicated. And an error thrown here can't avoid the previous purchases
        // done.
        log.error("Request for Supplier: {} Failed!", purchase.getSupplier().getId());
      }
    }
  }

  /**
   * Updates the status of the purchase.
   *
   * @param correlation Correlation id.
   * @param message Message payload.
   */
  public void updateStatus(String correlation, String message) {
    log.info("Message received with Correlation: {} | Body: {}", correlation, message);
    try {
      AdapterResponse response = mapper.readerFor(AdapterResponse.class).readValue(message);
      JavaxValidator.validate(response);
      PurchaseEntity purchase =
          purchaseRepository.findPurchaseEntityByPurchaseId(response.getPurchaseId());
      purchase.setStatus(PurchaseStatus.ORDERED);
      purchase.setSupplierOrderId(response.getId());
      purchase.setUpdated(new Timestamp(System.currentTimeMillis()));
      // Persist changes
      purchaseRepository.save(purchase);
      log.info(
          "Updated Status for purchase: {} | Status: {} | SupplierOrderId: {} | Updated: {}",
          purchase.getPurchaseId(),
          purchase.getStatus(),
          purchase.getSupplierOrderId(),
          purchase.getUpdated().toString());
      sendToCustomer(purchase.getOrderId());
    } catch (Exception e) {
      log.error("Poisoned message! error: {}", e.getMessage());
      throw new AmqpRejectAndDontRequeueException("Poisoned Message");
    }
  }

  void sendToCustomer(String orderId) throws JsonProcessingException {
    List<PurchaseEntity> purchases =
        purchaseRepository.findByOrderIdPagination(
            orderId,
            PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "purchaseId"))));
    List<String> ids = new ArrayList<>();
    int count = 0;
    for (PurchaseEntity entity : purchases) {
      if (entity.getStatus().equals("ordered")) {
        count++;
        ids.add(entity.getPurchaseId());
      }
    }
    if (count == purchases.size()) {
      CustomerResponse response = new CustomerResponse(orderId, ids);
      rabbitTemplate.convertAndSend("purchase-customer", mapper.writeValueAsString(response));
    }
  }
}
