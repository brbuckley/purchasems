package purchasems.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import purchasems.PurchaseMsResponseUtil;
import purchasems.api.model.adapter.AdapterResponse;
import purchasems.api.model.customer.CustomerResponse;
import purchasems.api.model.order.OrderRequest;
import purchasems.api.model.orderline.OrderLineRequest;
import purchasems.api.model.product.ProductRequest;
import purchasems.api.model.product.ProductSupplier;
import purchasems.api.model.purchase.PurchaseResponse;
import purchasems.api.model.purchase.PurchaseStatus;
import purchasems.api.model.supplier.SupplierResponse;
import purchasems.configuration.AdaptersConfig;
import purchasems.configuration.AdaptersMap;
import purchasems.exception.NotExistException;
import purchasems.mapper.OrderLineMapper;
import purchasems.mapper.PurchaseMapper;
import purchasems.persistence.domain.OrderLineEntity;
import purchasems.persistence.domain.PurchaseEntity;
import purchasems.persistence.repository.OrderLineRepository;
import purchasems.persistence.repository.PurchaseRepository;

public class PurchaseServiceTest {

  PurchaseService service;

  @Test
  public void testGetPurchase_whenValid_thenReturnPurchase() throws NotExistException {
    // Mocks
    PurchaseRepository purchaseRepo = Mockito.mock(PurchaseRepository.class);
    when(purchaseRepo.findPurchaseEntityByPurchaseId("PUR0000001"))
        .thenReturn(PurchaseMsResponseUtil.defaultPurchaseEntity());
    OrderLineRepository orderLineRepository = Mockito.mock(OrderLineRepository.class);
    when(orderLineRepository.save(PurchaseMsResponseUtil.defaultOrderLineEntity()))
        .thenReturn(PurchaseMsResponseUtil.defaultOrderLineEntity());

    service =
        new PurchaseService(
            purchaseRepo,
            orderLineRepository,
            new PurchaseMapper(new OrderLineMapper()),
            new OrderLineMapper(),
            null,
            null,
            new ObjectMapper(),
            new AdaptersMap(
                new AdaptersConfig(
                    "localhost:1",
                    "SUP0000001",
                    "localhost:2",
                    "SUP0000002",
                    "localhost:3",
                    "SUP0000003")));

    assertEquals("ORD0000001", service.getPurchase("PUR0000001").getOrderId());
  }

  @Test
  public void testGetPurchase_whenNull_thenThrow() {
    // Mocks
    PurchaseRepository purchaseRepo = Mockito.mock(PurchaseRepository.class);
    when(purchaseRepo.findPurchaseEntityByPurchaseId("PUR0000001")).thenReturn(null);
    OrderLineRepository orderLineRepository = Mockito.mock(OrderLineRepository.class);
    when(orderLineRepository.save(PurchaseMsResponseUtil.defaultOrderLineEntity()))
        .thenReturn(PurchaseMsResponseUtil.defaultOrderLineEntity());

    service =
        new PurchaseService(
            purchaseRepo,
            orderLineRepository,
            new PurchaseMapper(new OrderLineMapper()),
            new OrderLineMapper(),
            null,
            null,
            new ObjectMapper(),
            new AdaptersMap(
                new AdaptersConfig(
                    "localhost:1",
                    "SUP0000001",
                    "localhost:2",
                    "SUP0000002",
                    "localhost:3",
                    "SUP0000003")));

    assertThrows(NotExistException.class, () -> service.getPurchase("PUR0000001"));
  }

  @Test
  public void testGetPurchases_whenValid_thenReturnList() throws NotExistException {
    Pageable pageable =
        PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "datetime")));
    // Mocks
    PurchaseRepository purchaseRepo = Mockito.mock(PurchaseRepository.class);
    when(purchaseRepo.findByOrderIdPagination("ORD0000001", pageable))
        .thenReturn(PurchaseMsResponseUtil.defaultPurchaseList());
    OrderLineRepository orderLineRepository = Mockito.mock(OrderLineRepository.class);
    when(orderLineRepository.save(PurchaseMsResponseUtil.defaultOrderLineEntity()))
        .thenReturn(PurchaseMsResponseUtil.defaultOrderLineEntity());

    service =
        new PurchaseService(
            purchaseRepo,
            orderLineRepository,
            new PurchaseMapper(new OrderLineMapper()),
            new OrderLineMapper(),
            null,
            null,
            new ObjectMapper(),
            new AdaptersMap(
                new AdaptersConfig(
                    "localhost:1",
                    "SUP0000001",
                    "localhost:2",
                    "SUP0000002",
                    "localhost:3",
                    "SUP0000003")));

    assertEquals(2, service.getPurchases("ORD0000001", "asc.datetime", 50, 0).size());
  }

  @Test
  public void testGetPurchases_whenNotFound_thenThrow() {
    Pageable pageable =
        PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "datetime")));
    // Mocks
    PurchaseRepository purchaseRepo = Mockito.mock(PurchaseRepository.class);
    when(purchaseRepo.findByOrderIdPagination("ORD0000001", pageable))
        .thenReturn(new ArrayList<>());
    OrderLineRepository orderLineRepository = Mockito.mock(OrderLineRepository.class);
    when(orderLineRepository.save(PurchaseMsResponseUtil.defaultOrderLineEntity()))
        .thenReturn(PurchaseMsResponseUtil.defaultOrderLineEntity());

    service =
        new PurchaseService(
            purchaseRepo,
            orderLineRepository,
            new PurchaseMapper(new OrderLineMapper()),
            new OrderLineMapper(),
            null,
            null,
            new ObjectMapper(),
            new AdaptersMap(
                new AdaptersConfig(
                    "localhost:1",
                    "SUP0000001",
                    "localhost:2",
                    "SUP0000002",
                    "localhost:3",
                    "SUP0000003")));

    assertThrows(
        NotExistException.class, () -> service.getPurchases("ORD0000001", "asc.datetime", 50, 0));
  }

  @Test
  public void testGetPurchases_whenSortDesc_thenGet() throws NotExistException {
    Pageable pageable =
        PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.DESC, "datetime")));
    // Mocks
    PurchaseRepository purchaseRepo = Mockito.mock(PurchaseRepository.class);
    when(purchaseRepo.findByOrderIdPagination("ORD0000001", pageable))
        .thenReturn(PurchaseMsResponseUtil.defaultPurchaseList());
    OrderLineRepository orderLineRepository = Mockito.mock(OrderLineRepository.class);
    when(orderLineRepository.save(PurchaseMsResponseUtil.defaultOrderLineEntity()))
        .thenReturn(PurchaseMsResponseUtil.defaultOrderLineEntity());

    service =
        new PurchaseService(
            purchaseRepo,
            orderLineRepository,
            new PurchaseMapper(new OrderLineMapper()),
            new OrderLineMapper(),
            null,
            null,
            new ObjectMapper(),
            new AdaptersMap(
                new AdaptersConfig(
                    "localhost:1",
                    "SUP0000001",
                    "localhost:2",
                    "SUP0000002",
                    "localhost:3",
                    "SUP0000003")));

    assertEquals(2, service.getPurchases("ORD0000001", "desc.datetime", 50, 0).size());
  }

  @Test
  public void testSortLists_whenValid_thenSort() throws JsonProcessingException {
    List<OrderLineRequest> items = new ArrayList<>();
    items.add(new OrderLineRequest(2, new ProductRequest("PRD0000004")));
    items.add(new OrderLineRequest(2, new ProductRequest("PRD0000005")));
    items.add(new OrderLineRequest(2, new ProductRequest("PRD0000002")));
    items.add(new OrderLineRequest(2, new ProductRequest("PRD0000003")));
    items.add(new OrderLineRequest(2, new ProductRequest("PRD0000001")));

    List<ProductSupplier> productSuppliers = new ArrayList<>();
    productSuppliers.add(
        new ProductSupplier(
            null, null, null, "PRD0000001", new SupplierResponse("SUP0000001", "Supplier A")));
    productSuppliers.add(
        new ProductSupplier(
            null, null, null, "PRD0000003", new SupplierResponse("SUP0000002", "Supplier B")));
    productSuppliers.add(
        new ProductSupplier(
            null, null, null, "PRD0000002", new SupplierResponse("SUP0000003", "Supplier C")));
    productSuppliers.add(
        new ProductSupplier(
            null, null, null, "PRD0000005", new SupplierResponse("SUP0000001", "Supplier A")));
    productSuppliers.add(
        new ProductSupplier(
            null, null, null, "PRD0000004", new SupplierResponse("SUP0000002", "Supplier B")));

    // Spy
    service = mock(PurchaseService.class, CALLS_REAL_METHODS);
    doReturn(null).when(service).splitPurchases(any(), any(), any(), any());
    doNothing().when(service).sendToAdapters(any(), anyString());

    service.splitAndSend(new OrderRequest("ORD0000001", items), productSuppliers, "correlation");
    // Verifies that all lists are sorted as expected.
    verify(service)
        .splitPurchases(
            PurchaseMsResponseUtil.defaultSortedSuppliers(),
            PurchaseMsResponseUtil.defaultSortedItems(),
            PurchaseMsResponseUtil.defaultSortedProducts(),
            "ORD0000001");
  }

  @Test
  public void testSendToAdapters_whenValidAndInvalid_thenSend() throws JsonProcessingException {
    List<PurchaseResponse> purchases = PurchaseMsResponseUtil.defaultPurchaseResponses();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Correlation-Id", "correlation");
    HttpEntity<String> request1 =
        new HttpEntity<>(new ObjectMapper().writeValueAsString(purchases.get(0)), headers);
    HttpEntity<String> request2 =
        new HttpEntity<>(new ObjectMapper().writeValueAsString(purchases.get(1)), headers);

    // Mocks
    RestTemplate mock = Mockito.mock(RestTemplate.class);
    when(mock.postForEntity("localhost:1", request1, String.class))
        .thenReturn(new ResponseEntity<String>(HttpStatus.OK));
    when(mock.postForEntity("localhost:2", request2, String.class))
        .thenReturn(new ResponseEntity<String>(HttpStatus.BAD_REQUEST));

    service =
        new PurchaseService(
            null,
            null,
            new PurchaseMapper(new OrderLineMapper()),
            null,
            mock,
            null,
            new ObjectMapper(),
            new AdaptersMap(
                new AdaptersConfig(
                    "localhost:1",
                    "SUP0000001",
                    "localhost:2",
                    "SUP0000002",
                    "localhost:3",
                    "SUP0000003")));
    assertDoesNotThrow(
        () -> service.sendToAdapters(PurchaseMsResponseUtil.defaultPurchaseList(), "correlation"));
  }

  @Test
  public void testSendToAdapters_whenAdapterIsDown_thenCatchException()
      throws JsonProcessingException {
    List<PurchaseResponse> purchases = PurchaseMsResponseUtil.defaultPurchaseResponses();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Correlation-Id", "correlation");
    HttpEntity<String> request1 =
        new HttpEntity<>(new ObjectMapper().writeValueAsString(purchases.get(0)), headers);

    // Mocks
    RestTemplate mock = Mockito.mock(RestTemplate.class);
    when(mock.postForEntity("localhost:1", request1, String.class))
        .thenThrow(new RestClientException("mock"));

    service =
        new PurchaseService(
            null,
            null,
            new PurchaseMapper(new OrderLineMapper()),
            null,
            mock,
            null,
            new ObjectMapper(),
            null);
    assertDoesNotThrow(
        () -> service.sendToAdapters(PurchaseMsResponseUtil.defaultPurchaseList(), "correlation"));
  }

  @Test
  public void testUpdateStatus_whenValid_thenUpdate() throws JsonProcessingException {
    List<PurchaseEntity> purchases = new ArrayList<>();
    purchases.add(PurchaseMsResponseUtil.defaultPurchaseEntity());
    PurchaseEntity entity = PurchaseMsResponseUtil.defaultPurchaseEntity();
    entity.setStatus(PurchaseStatus.ORDERED);
    entity.setSupplierOrderId("SUA0000001");
    // Mocks
    PurchaseRepository mockRepo = Mockito.spy(PurchaseRepository.class);
    when(mockRepo.findPurchaseEntityByPurchaseId("PUR0000001"))
        .thenReturn(PurchaseMsResponseUtil.defaultPurchaseEntity());
    when(mockRepo.save(any())).thenReturn(entity);
    when(mockRepo.findByOrderIdPagination(
            "ORD0000001",
            PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "purchaseId")))))
        .thenReturn(purchases);
    ObjectReader mock = Mockito.mock(ObjectReader.class);
    when(mock.readValue("{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}"))
        .thenReturn(new AdapterResponse("SUA0000001", "PUR0000001"));
    ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
    when(mockMapper.readerFor(AdapterResponse.class)).thenReturn(mock);

    service = new PurchaseService(mockRepo, null, null, null, null, null, mockMapper, null);
    service.updateStatus("correlation", "{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}");

    verify(mockRepo).save(any());
  }

  @Test
  public void testUpdateStatus_whenInvalid_thenThrow() throws JsonProcessingException {
    PurchaseEntity entity = PurchaseMsResponseUtil.defaultPurchaseEntity();
    entity.setStatus(PurchaseStatus.ORDERED);
    List<PurchaseEntity> purchases = new ArrayList<>();
    purchases.add(entity);
    List<String> ids = new ArrayList<>();
    ids.add("PUR0000001");
    // Mocks
    PurchaseRepository mockRepo = Mockito.mock(PurchaseRepository.class);
    when(mockRepo.findPurchaseEntityByPurchaseId("PUR0000001")).thenReturn(null);
    when(mockRepo.findByOrderIdPagination(
            "ORD0000001",
            PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "purchaseId")))))
        .thenReturn(purchases);
    ObjectReader mock = Mockito.mock(ObjectReader.class);
    when(mock.readValue("{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}"))
        .thenReturn(new AdapterResponse("SUA0000001", "PUR0000001"));
    ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
    when(mockMapper.readerFor(AdapterResponse.class)).thenReturn(mock);
    when(mockMapper.writeValueAsString(new CustomerResponse("ORD0000001", ids))).thenReturn(null);
    RabbitTemplate mockRabbit = Mockito.mock(RabbitTemplate.class);

    service = new PurchaseService(mockRepo, null, null, null, null, mockRabbit, mockMapper, null);
    assertThrows(
        AmqpRejectAndDontRequeueException.class,
        () ->
            service.updateStatus(
                "correlation", "{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}"));
  }

  @Test
  public void testSplitPurchases_whenValid_thenSplit() {
    // Mocks
    PurchaseRepository mockPurchaseRepo = mock(PurchaseRepository.class);
    when(mockPurchaseRepo.save(any())).thenReturn(new PurchaseEntity());
    OrderLineRepository mockOrderLineRepo = mock(OrderLineRepository.class);
    when(mockOrderLineRepo.save(any())).thenReturn(new OrderLineEntity());

    service =
        new PurchaseService(
            mockPurchaseRepo,
            mockOrderLineRepo,
            null,
            new OrderLineMapper(),
            null,
            null,
            null,
            null);
    List<PurchaseEntity> result =
        service.splitPurchases(
            PurchaseMsResponseUtil.defaultSortedSuppliers(),
            PurchaseMsResponseUtil.defaultSortedItems(),
            PurchaseMsResponseUtil.defaultSortedProducts(),
            "ORD0000001");

    assertEquals(3, result.size());
  }

  @Test
  public void testSendToCustomer_whenProcessing_thenDoNotSend() throws JsonProcessingException {
    PurchaseEntity entity = PurchaseMsResponseUtil.anotherPurchaseEntity();
    entity.setStatus(PurchaseStatus.ORDERED);
    List<PurchaseEntity> purchases = new ArrayList<>();
    purchases.add(PurchaseMsResponseUtil.defaultPurchaseEntity());
    purchases.add(entity);

    PurchaseRepository mockPurchaseRepo = mock(PurchaseRepository.class);
    when(mockPurchaseRepo.findByOrderIdPagination(
            "ORD0000001",
            PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "purchaseId")))))
        .thenReturn(purchases);
    RabbitTemplate mockRabbit = Mockito.spy(RabbitTemplate.class);

    service =
        new PurchaseService(
            mockPurchaseRepo, null, null, null, null, mockRabbit, new ObjectMapper(), null);
    service.sendToCustomer("ORD0000001");

    verify(mockRabbit, never()).convertAndSend(anyString(), anyString());
  }

  @Test
  public void testSendToCustomer_whenOrdered_thenSend() throws JsonProcessingException {
    PurchaseEntity entity = PurchaseMsResponseUtil.defaultPurchaseEntity();
    entity.setStatus(PurchaseStatus.ORDERED);
    PurchaseEntity anotherEntity = PurchaseMsResponseUtil.anotherPurchaseEntity();
    anotherEntity.setStatus(PurchaseStatus.ORDERED);
    List<PurchaseEntity> purchases = new ArrayList<>();
    purchases.add(entity);
    purchases.add(anotherEntity);

    PurchaseRepository mockPurchaseRepo = mock(PurchaseRepository.class);
    when(mockPurchaseRepo.findByOrderIdPagination(
            "ORD0000001",
            PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "purchaseId")))))
        .thenReturn(purchases);
    RabbitTemplate mockRabbit = Mockito.spy(RabbitTemplate.class);
    doNothing().when(mockRabbit).convertAndSend(anyString(), anyString());

    service =
        new PurchaseService(
            mockPurchaseRepo, null, null, null, null, mockRabbit, new ObjectMapper(), null);
    service.sendToCustomer("ORD0000001");

    verify(mockRabbit)
        .convertAndSend(
            "purchase-customer",
            "{\"order_id\":\"ORD0000001\",\"purchases\":[\"PUR0000001\",\"PUR0000002\"]}");
  }
}
