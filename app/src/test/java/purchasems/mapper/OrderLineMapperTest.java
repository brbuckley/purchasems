package purchasems.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import purchasems.PurchaseMsResponseUtil;
import purchasems.api.model.product.ProductSupplier;
import purchasems.persistence.domain.OrderLineEntity;

public class OrderLineMapperTest {

  @Test
  public void testToEntity_whenCategory_thenParse() {
    OrderLineMapper mapper = new OrderLineMapper();
    OrderLineEntity entity = new OrderLineEntity();
    mapper.toEntity(PurchaseMsResponseUtil.defaultProductSupplier(), 1, entity);
    assertEquals("PRD0000001", entity.getProductId());
  }

  @Test
  public void testToEntity_whenNullCategory_thenParse() {
    OrderLineMapper mapper = new OrderLineMapper();
    OrderLineEntity entity = new OrderLineEntity();
    ProductSupplier productSupplier = PurchaseMsResponseUtil.defaultProductSupplier();
    productSupplier.setCategory(null);
    mapper.toEntity(productSupplier, 1, entity);
    assertNull(entity.getCategory());
  }
}
