package purchasems.mapper;

import org.springframework.stereotype.Component;
import purchasems.api.model.orderline.OrderLineResponse;
import purchasems.api.model.product.ProductCategory;
import purchasems.api.model.product.ProductResponse;
import purchasems.api.model.product.ProductSupplier;
import purchasems.persistence.domain.OrderLineEntity;

/** Mapper for OrderLine related objects. */
@Component
public class OrderLineMapper {

  /**
   * Parses from OrderLineEntity to OrderLineResponse.
   *
   * @param entity OrderLine Entity.
   * @return OrderLine Response.
   */
  public OrderLineResponse fromOrderLineEntity(OrderLineEntity entity) {
    return new OrderLineResponse(
        entity.getQuantity(),
        new ProductResponse(
            entity.getProductId(),
            entity.getName(),
            entity.getPrice(),
            entity.getCategory().getValue()));
  }

  /**
   * Parses from ProductSupplier and Quantity to OrderLineEntity.
   *
   * @param productSupplier Product Supplier object coming from ProductMS.
   * @param quantity Quantity of items.
   * @param entity OrderLine Entity.
   */
  public void toEntity(ProductSupplier productSupplier, int quantity, OrderLineEntity entity) {
    entity.setQuantity(quantity);
    entity.setProductId(productSupplier.getId());
    entity.setName(productSupplier.getName());
    entity.setPrice(productSupplier.getPrice());
    entity.setCategory(
        productSupplier.getCategory() == null
            ? null
            : ProductCategory.fromValue(productSupplier.getCategory()));
  }
}
