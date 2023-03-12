package purchasems.api.model.orderline;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import purchasems.api.model.product.ProductResponse;

/** OrderLine Response Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonPropertyOrder({"quantity", "product"})
public class OrderLineResponse {

  private int quantity;
  private ProductResponse product;
}
