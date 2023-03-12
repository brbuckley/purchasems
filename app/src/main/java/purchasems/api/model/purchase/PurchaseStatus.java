package purchasems.api.model.purchase;

import java.util.Arrays;
import java.util.Locale;

/** The status of a purchase order. */
public enum PurchaseStatus {
  PROCESSING,
  ORDERED;

  public String getValue() {
    return this.toString().toLowerCase(Locale.ROOT);
  }

  /**
   * Builds a Status from a name.
   *
   * @param text Status name lowercase.
   * @return Status.
   */
  public static PurchaseStatus fromValue(String text) {
    String normalized = text.toUpperCase();
    return Arrays.stream(PurchaseStatus.values())
        .filter(g -> g.name().startsWith(normalized))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No status starting with " + text));
  }
}
