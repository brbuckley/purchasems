package purchasems.configuration;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Utility class to wrap the adapter configs into a more manageable HashMap. */
@Getter
@Component
public class AdaptersMap {

  private Map<String, String> endpoints;
  private AdaptersConfig adaptersConfig;

  /**
   * Custom Constructor that also populates the map.
   *
   * @param adaptersConfig Adapter Configuration from application properties.
   */
  @Autowired
  public AdaptersMap(AdaptersConfig adaptersConfig) {
    this.adaptersConfig = adaptersConfig;
    this.endpoints = new HashMap<>();
    this.endpoints.put(adaptersConfig.getSupplierAid(), adaptersConfig.getSupplierAendpoint());
    this.endpoints.put(adaptersConfig.getSupplierBid(), adaptersConfig.getSupplierBendpoint());
    this.endpoints.put(adaptersConfig.getSupplierCid(), adaptersConfig.getSupplierCendpoint());
  }
}
