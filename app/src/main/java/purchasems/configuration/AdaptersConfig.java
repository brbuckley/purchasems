package purchasems.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Property injection for ProductMs. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "adapters")
public class AdaptersConfig {

  // Maybe change this to 2 strings containing all the suppliers and use split() to get each one.
  // This way its more scalable to new suppliers
  private String supplierAendpoint;
  private String supplierAid;
  private String supplierBendpoint;
  private String supplierBid;
  private String supplierCendpoint;
  private String supplierCid;
}
