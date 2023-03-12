package purchasems.api.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import purchasems.api.model.orderline.OrderLineRequest;

/** Order Request Model. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({"order_id", "items"})
public class OrderRequest {

  @NotBlank
  @Pattern(regexp = "^ORD[0-9]{7}$")
  @JsonProperty(value = "order_id")
  private String orderId;

  @NotEmpty private List<OrderLineRequest> items;

  /**
   * Utility method to get a list of Ids from the items.
   *
   * @return List of Product ids.
   */
  @JsonIgnore
  public List<String> getIds() {
    List<String> ids = new ArrayList<>();
    for (OrderLineRequest orderLine : items) {
      ids.add(orderLine.getProduct().getId());
    }
    return ids;
  }
}
