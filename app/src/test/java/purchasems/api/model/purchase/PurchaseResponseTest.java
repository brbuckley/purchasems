package purchasems.api.model.purchase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import purchasems.PurchaseMsResponseUtil;
import purchasems.api.model.orderline.OrderLineResponse;
import purchasems.api.model.product.ProductResponse;
import purchasems.api.model.supplier.SupplierResponse;

public class PurchaseResponseTest {

  @Test
  public void testConstructor_whenNull_thenConstruct() {
    PurchaseResponse response =
        new PurchaseResponse(null, null, null, null, null, null, null, null);
    assertNull(response.getDatetime());
  }

  @Test
  public void testConstructor_whenValid_thenConstruct() {
    List<OrderLineResponse> items = new ArrayList<>();
    items.add(
        new OrderLineResponse(
            1, new ProductResponse("PRD0000001", "Heineken", new BigDecimal("10"), "beer")));
    PurchaseResponse response =
        new PurchaseResponse(
            "PUR0000001",
            "ORD0000001",
            new SupplierResponse("SUP0000001", "Supplier A"),
            "processing",
            Timestamp.valueOf("2022-07-29 12:16:00"),
            Timestamp.valueOf("2022-07-29 12:17:00"),
            items,
            "SUA0000001");
    assertEquals("2022-07-29 12:17:00.0", response.getUpdated().toString());
  }

  @Test
  public void testSetDatetime_whenValid_thenGet() {
    PurchaseResponse response = PurchaseMsResponseUtil.defaultPurchaseResponse();
    response.setDatetime(Timestamp.valueOf("2022-07-29 12:16:00"));
    assertEquals("2022-07-29 12:16:00.0", response.getDatetime().toString());
  }

  @Test
  public void testGetDatetime_whenNull_thenGet() {
    PurchaseResponse response = PurchaseMsResponseUtil.defaultPurchaseResponse();
    response.setDatetime(null);
    assertNull(response.getDatetime());
  }

  @Test
  public void testSetUpdated_whenValid_thenGet() {
    PurchaseResponse response = PurchaseMsResponseUtil.defaultPurchaseResponse();
    response.setUpdated(Timestamp.valueOf("2022-07-29 12:16:00"));
    assertEquals("2022-07-29 12:16:00.0", response.getUpdated().toString());
  }

  @Test
  public void testGetUpdated_whenNull_thenGet() {
    PurchaseResponse response = PurchaseMsResponseUtil.defaultPurchaseResponse();
    response.setUpdated(null);
    assertNull(response.getUpdated());
  }
}
