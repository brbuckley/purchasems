package purchasems.mapper;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import purchasems.api.model.orderline.OrderLineResponse;
import purchasems.api.model.purchase.PurchaseResponse;
import purchasems.api.model.supplier.SupplierResponse;
import purchasems.persistence.domain.OrderLineEntity;
import purchasems.persistence.domain.PurchaseEntity;

/** Mapper for Purchase related objects. */
@NoArgsConstructor
@Component
public class PurchaseMapper {

  private OrderLineMapper mapper;

  @Autowired
  public PurchaseMapper(OrderLineMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Parses from Purchase Entity to Purchase Response.
   *
   * @param entity Purchase Entity.
   * @return Purchase Response.
   */
  public PurchaseResponse fromEntity(PurchaseEntity entity) {
    List<OrderLineResponse> items = new ArrayList<>();
    for (OrderLineEntity orderLine : entity.getItems()) {
      items.add(mapper.fromOrderLineEntity(orderLine));
    }
    return new PurchaseResponse(
        entity.getPurchaseId(),
        entity.getOrderId(),
        new SupplierResponse(entity.getSupplierId(), entity.getSupplierName()),
        entity.getStatus(),
        entity.getDatetime(),
        entity.getUpdated(),
        items,
        entity.getSupplierOrderId());
  }
}
