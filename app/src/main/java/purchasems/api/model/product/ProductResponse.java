package purchasems.api.model.product;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Product Response Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonPropertyOrder({"id", "name", "price", "category"})
public class ProductResponse {

  private String id;
  private String name;
  private BigDecimal price;
  private String category;
}
