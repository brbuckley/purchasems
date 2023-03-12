package purchasems.api.model.product;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import purchasems.api.model.supplier.SupplierResponse;

/** Product Supplier Model. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductSupplier {

  private String name;
  private BigDecimal price;
  private String category;
  private String id;
  private SupplierResponse supplier;
}
