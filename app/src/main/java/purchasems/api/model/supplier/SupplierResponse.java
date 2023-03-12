package purchasems.api.model.supplier;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Supplier Response Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({"id", "name"})
public class SupplierResponse {

  @NotBlank
  @Pattern(regexp = "^SUP[0-9]{7}$")
  private String id;

  private String name;
}
