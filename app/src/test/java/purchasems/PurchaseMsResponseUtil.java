package purchasems;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import purchasems.api.model.order.OrderRequest;
import purchasems.api.model.orderline.OrderLineRequest;
import purchasems.api.model.orderline.OrderLineResponse;
import purchasems.api.model.product.ProductCategory;
import purchasems.api.model.product.ProductRequest;
import purchasems.api.model.product.ProductResponse;
import purchasems.api.model.product.ProductSupplier;
import purchasems.api.model.purchase.PurchaseRequest;
import purchasems.api.model.purchase.PurchaseResponse;
import purchasems.api.model.purchase.PurchaseStatus;
import purchasems.api.model.supplier.SupplierResponse;
import purchasems.persistence.domain.OrderLineEntity;
import purchasems.persistence.domain.PurchaseEntity;

public class PurchaseMsResponseUtil {

  public static PurchaseEntity defaultPurchaseEntity() {
    List<OrderLineEntity> items = new ArrayList<>();
    items.add(defaultOrderLineEntity());
    return new PurchaseEntity(
        1,
        "PUR0000001",
        "ORD0000001",
        PurchaseStatus.PROCESSING,
        Timestamp.valueOf("2022-07-29 12:16:00"),
        null,
        items,
        "SUP0000001",
        "Supplier A");
  }

  public static OrderLineEntity defaultOrderLineEntity() {
    return new OrderLineEntity(
        1, 2, "PRD0000001", "Heineken", new BigDecimal("10"), ProductCategory.BEER, null);
  }

  public static PurchaseResponse defaultPurchaseResponse() {
    List<OrderLineResponse> items = new ArrayList<>();
    items.add(defaultOrderLineResponse());
    return new PurchaseResponse(
        "PUR0000001",
        "ORD0000001",
        defaultSupplierResponse(),
        "processing",
        Timestamp.valueOf("2022-07-29 12:16:00"),
        null,
        items,
        null);
  }

  public static List<PurchaseResponse> defaultPurchaseListResponse() {
    List<PurchaseResponse> response = new ArrayList<>();
    response.add(defaultPurchaseResponse());
    return response;
  }

  public static OrderLineResponse defaultOrderLineResponse() {
    return new OrderLineResponse(2, defaultProductResponse());
  }

  public static OrderLineResponse anotherOrderLineResponse() {
    return new OrderLineResponse(3, anotherProductResponse());
  }

  public static ProductResponse defaultProductResponse() {
    return new ProductResponse("PRD0000001", "Heineken", new BigDecimal("10"), "beer");
  }

  public static ProductResponse anotherProductResponse() {
    return new ProductResponse("PRD0000003", "Itaipólvora", new BigDecimal("0.99"), "beer");
  }

  public static SupplierResponse defaultSupplierResponse() {
    return new SupplierResponse("SUP0000001", "Supplier A");
  }

  public static SupplierResponse anotherSupplierResponse() {
    return new SupplierResponse("SUP0000002", "Supplier B");
  }

  public static PurchaseRequest defaultPurchaseRequest() {
    List<OrderLineRequest> items = new ArrayList<>();
    items.add(defaultOrderLineRequest());
    return new PurchaseRequest("ORD0000001", items);
  }

  public static OrderLineRequest defaultOrderLineRequest() {
    return new OrderLineRequest(2, defaultProductRequest());
  }

  public static OrderLineRequest anotherOrderLineRequest() {
    return new OrderLineRequest(3, anotherProductRequest());
  }

  public static ProductRequest defaultProductRequest() {
    return new ProductRequest("PRD0000001");
  }

  public static ProductRequest anotherProductRequest() {
    return new ProductRequest("PRD0000003");
  }

  public static List<PurchaseEntity> defaultPurchaseList() {
    List<PurchaseEntity> list = new ArrayList<>();
    list.add(defaultPurchaseEntity());
    list.add(anotherPurchaseEntity());
    return list;
  }

  public static List<PurchaseResponse> defaultPurchaseResponses() {
    List<PurchaseResponse> responses = new ArrayList<>();
    List<OrderLineResponse> orderLines1 = new ArrayList<>();
    orderLines1.add(defaultOrderLineResponse());
    List<OrderLineResponse> orderLines2 = new ArrayList<>();
    orderLines2.add(anotherOrderLineResponse());
    PurchaseResponse response1 =
        new PurchaseResponse(
            "PUR0000001",
            "ORD0000001",
            defaultSupplierResponse(),
            "processing",
            Timestamp.valueOf("2022-07-29 12:16:00"),
            null,
            orderLines1,
            null);
    PurchaseResponse response2 =
        new PurchaseResponse(
            "PUR0000002",
            "ORD0000001",
            anotherSupplierResponse(),
            "processing",
            Timestamp.valueOf("2022-07-29 12:16:00"),
            null,
            orderLines2,
            null);
    responses.add(response1);
    responses.add(response2);

    return responses;
  }

  public static PurchaseEntity anotherPurchaseEntity() {
    List<OrderLineEntity> items = new ArrayList<>();
    items.add(
        new OrderLineEntity(
            3, 3, "PRD0000003", "Itaipólvora", new BigDecimal("0.99"), ProductCategory.BEER, null));
    return new PurchaseEntity(
        2,
        "PUR0000002",
        "ORD0000001",
        PurchaseStatus.PROCESSING,
        Timestamp.valueOf("2022-07-29 12:16:00"),
        null,
        items,
        "SUP0000002",
        "Supplier B");
  }

  public static OrderRequest defaultOrderRequest() {
    List<OrderLineRequest> items = new ArrayList<>();
    items.add(defaultOrderLineRequest());
    return new OrderRequest("ORD0000001", items);
  }

  public static OrderRequest anotherOrderRequest() {
    List<OrderLineRequest> items = new ArrayList<>();
    items.add(defaultOrderLineRequest());
    items.add(anotherOrderLineRequest());
    return new OrderRequest("ORD0000001", items);
  }

  public static String productMsResponse() {
    return "[{\"name\":\"Amstel\",\"price\":4.99,\"category\":\"beer\",\"id\":\"PRD0000002\","
        + "\"supplier\":{\"id\":\"SUP0000001\",\"name\":\"Supplier A\"}},"
        + "{\"name\":\"Heineken\",\"price\":10,\"category\":\"beer\",\"id\":\"PRD0000001\","
        + "\"supplier\":{\"id\":\"SUP0000001\",\"name\":\"Supplier A\"}},"
        + "{\"name\":\"Itaipólvora\",\"price\":0.99,\"category\":\"beer\",\"id\":\"PRD0000003\","
        + "\"supplier\":{\"id\":\"SUP0000002\",\"name\":\"Supplier B\"}}]";
  }

  public static List<ProductSupplier> productSupplierList() {
    List<ProductSupplier> products = new ArrayList<>();
    products.add(
        new ProductSupplier(
            "Amstel",
            new BigDecimal("4.99"),
            "beer",
            "PRD0000002",
            new SupplierResponse("SUP0000001", "Supplier A")));
    products.add(
        new ProductSupplier(
            "Heineken",
            new BigDecimal("10"),
            "beer",
            "PRD0000001",
            new SupplierResponse("SUP0000001", "Supplier A")));
    products.add(
        new ProductSupplier(
            "Itaipólvora",
            new BigDecimal("0.99"),
            "beer",
            "PRD0000003",
            new SupplierResponse("SUP0000002", "Supplier B")));
    return products;
  }

  public static List<String> productIdList() {
    List<String> ids = new ArrayList<>();
    ids.add("PRD0000001");
    ids.add("PRD0000003");
    ids.add("PRD0000002");
    return ids;
  }

  public static ProductSupplier defaultProductSupplier() {
    return new ProductSupplier(
        "Heineken", new BigDecimal("10"), "beer", "PRD0000001", defaultSupplierResponse());
  }

  public static ProductSupplier anotherProductSupplier() {
    return new ProductSupplier(
        "Itaipólvora", new BigDecimal("0.99"), "beer", "PRD0000003", anotherSupplierResponse());
  }

  public static List<SupplierResponse> defaultSortedSuppliers() {
    List<SupplierResponse> sortedSuppliers = new ArrayList<>();
    sortedSuppliers.add(new SupplierResponse("SUP0000001", "Supplier A"));
    sortedSuppliers.add(new SupplierResponse("SUP0000002", "Supplier B"));
    sortedSuppliers.add(new SupplierResponse("SUP0000003", "Supplier C"));
    return sortedSuppliers;
  }

  public static List<OrderLineRequest> defaultSortedItems() {
    List<OrderLineRequest> sortedItems = new ArrayList<>();
    sortedItems.add(new OrderLineRequest(2, new ProductRequest("PRD0000001")));
    sortedItems.add(new OrderLineRequest(2, new ProductRequest("PRD0000002")));
    sortedItems.add(new OrderLineRequest(2, new ProductRequest("PRD0000003")));
    sortedItems.add(new OrderLineRequest(2, new ProductRequest("PRD0000004")));
    sortedItems.add(new OrderLineRequest(2, new ProductRequest("PRD0000005")));
    return sortedItems;
  }

  public static List<ProductSupplier> defaultSortedProducts() {
    List<ProductSupplier> sortedProducts = new ArrayList<>();
    sortedProducts.add(
        new ProductSupplier(
            null, null, null, "PRD0000001", new SupplierResponse("SUP0000001", "Supplier A")));
    sortedProducts.add(
        new ProductSupplier(
            null, null, null, "PRD0000002", new SupplierResponse("SUP0000003", "Supplier C")));
    sortedProducts.add(
        new ProductSupplier(
            null, null, null, "PRD0000003", new SupplierResponse("SUP0000002", "Supplier B")));
    sortedProducts.add(
        new ProductSupplier(
            null, null, null, "PRD0000004", new SupplierResponse("SUP0000002", "Supplier B")));
    sortedProducts.add(
        new ProductSupplier(
            null, null, null, "PRD0000005", new SupplierResponse("SUP0000001", "Supplier A")));
    return sortedProducts;
  }

  public static HttpEntity<String> defaultTokenRequest() {
    String payload =
        "{\"client_id\":\"id\",\"client_secret\":\"secret\",\"audience\":\"audience\",\"grant_type\":\"client_credentials\"}";
    HttpHeaders tokenHeaders = new HttpHeaders();
    tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<String>(payload, tokenHeaders);
  }

  public static HttpEntity<Void> defaultRequestHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer token");
    return new HttpEntity<>(headers);
  }

  public static HttpEntity<Void> anotherRequestHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer new token");
    return new HttpEntity<>(headers);
  }
}
