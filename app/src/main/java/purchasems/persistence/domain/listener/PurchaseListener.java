package purchasems.persistence.domain.listener;

import javax.persistence.PostPersist;
import purchasems.persistence.domain.PurchaseEntity;

/** Listener for the Purchase Entity. */
public class PurchaseListener {

  /**
   * Creates the customerId based on the DB id. It works like a DB trigger.
   *
   * @param purchase Purchase persisted.
   */
  @PostPersist
  public void process(PurchaseEntity purchase) {
    purchase.setPurchaseId(purchase.getId());
  }
}
