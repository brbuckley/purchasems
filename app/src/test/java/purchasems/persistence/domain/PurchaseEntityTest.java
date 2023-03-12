package purchasems.persistence.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import purchasems.PurchaseMsResponseUtil;
import purchasems.api.model.product.ProductCategory;
import purchasems.api.model.purchase.PurchaseStatus;

public class PurchaseEntityTest {

  @Test
  public void testConstructor_whenNull_thenConstruct() {
    PurchaseEntity entity = new PurchaseEntity(0, null, null, null, null, null, null, null, null);
    assertNull(entity.getStatus());
  }

  @Test
  public void testSetStatus_whenValid_thenSet() {
    PurchaseEntity entity = PurchaseMsResponseUtil.defaultPurchaseEntity();
    entity.setStatus(PurchaseStatus.ORDERED);
    assertEquals("ordered", entity.getStatus());
  }

  @Test
  public void testSetStatus_whenNull_thenSet() {
    PurchaseEntity entity = PurchaseMsResponseUtil.defaultPurchaseEntity();
    PurchaseStatus status = null;
    entity.setStatus(status);
    assertNull(entity.getStatus());
  }

  @Test
  public void testSetStatus_whenString_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setStatus("ordered");
    assertEquals("ordered", entity.getStatus());
  }

  @Test
  public void testSetStatus_whenNullString_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    String status = null;
    entity.setStatus(status);
    assertNull(entity.getStatus());
  }

  @Test
  public void testSetPurchaseId_whenInt_thenSet() {
    PurchaseEntity entity = PurchaseMsResponseUtil.defaultPurchaseEntity();
    entity.setPurchaseId(1);
    assertEquals("PUR0000001", entity.getPurchaseId());
  }

  @Test
  public void testSetDatetime_whenNull_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setDatetime(null);
    assertNull(entity.getDatetime());
  }

  @Test
  public void testSetSupplierId_whenValid_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setSupplierId("SUP0000001");
    assertEquals("SUP0000001", entity.getSupplierId());
  }

  @Test
  public void testSetSupplierId_whenInteger_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setSupplierId(1);
    assertEquals("SUP0000001", entity.getSupplierId());
  }

  @Test
  public void testSetPurchaseId_whenString_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setPurchaseId("PUR0000001");
    assertEquals("PUR0000001", entity.getPurchaseId());
  }

  @Test
  public void testSetUpdated_whenValid_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setUpdated(Timestamp.valueOf("2022-07-29 12:16:00"));
    assertEquals("2022-07-29 12:16:00.0", entity.getUpdated().toString());
  }

  @Test
  public void testSetUpdated_whenNull_thenSet() {
    PurchaseEntity entity = new PurchaseEntity();
    entity.setUpdated(null);
    assertNull(entity.getUpdated());
  }

  @Test
  public void testConstructor_whenValid_thenBuild() {
    List<OrderLineEntity> items = new ArrayList<>();
    items.add(
        new OrderLineEntity(
            1, 1, "PRD0000001", "Heineken", new BigDecimal("10"), ProductCategory.BEER, null));
    PurchaseEntity entity =
        new PurchaseEntity(
            1,
            "PUR0000001",
            "ORD0000001",
            PurchaseStatus.PROCESSING,
            Timestamp.valueOf("2022-07-29 12:16:00"),
            Timestamp.valueOf("2022-07-29 12:17:00"),
            items,
            "SUP0000001",
            "Supplier A");
    assertEquals("2022-07-29 12:17:00.0", entity.getUpdated().toString());
  }
}
