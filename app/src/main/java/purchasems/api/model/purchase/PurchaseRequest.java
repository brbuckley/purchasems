package purchasems.api.model.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import purchasems.api.model.orderline.OrderLineRequest;

/** Purchase Request Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PurchaseRequest {

  @NotBlank
  @Pattern(regexp = "^PUR[0-9]{7}$")
  @JsonProperty(value = "order_id")
  private String orderId;

  @NotEmpty private List<OrderLineRequest> items;
}
