package purchasems.api.model.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Adapter Response Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AdapterResponse {

  @NotBlank
  @Pattern(regexp = "^SU.[0-9]{7}$")
  private String id;

  @NotBlank
  @Pattern(regexp = "^PUR[0-9]{7}$")
  @JsonProperty(value = "purchase_id")
  private String purchaseId;
}
