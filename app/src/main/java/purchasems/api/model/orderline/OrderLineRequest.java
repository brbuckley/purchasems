package purchasems.api.model.orderline;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import purchasems.api.model.product.ProductRequest;

/** OrderLine Request Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OrderLineRequest {

  @Positive private int quantity;
  @NotNull private ProductRequest product;
}
