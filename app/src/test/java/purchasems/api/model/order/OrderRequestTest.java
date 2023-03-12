package purchasems.api.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import purchasems.PurchaseMsResponseUtil;

public class OrderRequestTest {

  @Test
  public void testGetIds_whenValid_thenGet() {
    OrderRequest request = PurchaseMsResponseUtil.defaultOrderRequest();
    List<String> ids = request.getIds();
    assertEquals("PRD0000001", ids.get(0));
  }
}
