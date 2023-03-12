package purchasems.api.model.purchase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PurchaseStatusTest {

  @Test
  public void testFromValue_whenInvalid_thenThrow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PurchaseStatus status = PurchaseStatus.fromValue("invalid");
        });
  }

  @Test
  public void testFromValue_whenValid_thenCreate() {
    PurchaseStatus status = PurchaseStatus.fromValue("ordered");
    assertEquals("ordered", status.getValue());
  }
}
