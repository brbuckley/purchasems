package purchasems.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import purchasems.api.model.purchase.PurchaseStatus;
import purchasems.persistence.domain.listener.PurchaseListener;

/** Purchase Order entity. */
@NoArgsConstructor
@Getter
@ToString
@Entity
@EntityListeners(PurchaseListener.class)
@Table(name = "purchase")
public class PurchaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String purchaseId;
  @Setter private String orderId;
  private PurchaseStatus status;
  private Timestamp datetime;
  private Timestamp updated;
  private String supplierId;
  @Setter private String supplierName;
  @Setter private String supplierOrderId;

  @OneToMany(mappedBy = "purchase", cascade = CascadeType.REMOVE)
  private List<OrderLineEntity> items = new ArrayList<OrderLineEntity>();

  /**
   * Custom constructor.
   *
   * @param id Id.
   * @param purchaseId Purchase id.
   * @param orderId Order id.
   * @param status Purchase status.
   * @param datetime Timestamp.
   * @param updated Time when the purchase was updated.
   * @param items List of OrderLines.
   * @param supplierId Supplier id.
   * @param supplierName Supplier name.
   */
  public PurchaseEntity(
      int id,
      String purchaseId,
      String orderId,
      PurchaseStatus status,
      Timestamp datetime,
      Timestamp updated,
      List<OrderLineEntity> items,
      String supplierId,
      String supplierName) {
    this.id = id;
    this.purchaseId = purchaseId;
    this.orderId = orderId;
    this.status = status;
    this.datetime = datetime == null ? null : new Timestamp(datetime.getTime());
    this.updated = updated == null ? null : new Timestamp(updated.getTime());
    this.items = items;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.supplierOrderId = null;
  }

  public void setSupplierId(int id) {
    this.supplierId = "SUP" + String.format("%07d", id);
  }

  public void setSupplierId(String id) {
    this.supplierId = id;
  }

  public String getStatus() {
    return this.status == null ? null : this.status.getValue();
  }

  public void setStatus(String status) {
    this.status = status == null ? null : PurchaseStatus.fromValue(status);
  }

  public void setStatus(PurchaseStatus status) {
    this.status = status;
  }

  // This is more of a utility setter
  public void setPurchaseId(int id) {
    this.purchaseId = "PUR" + String.format("%07d", id);
  }

  public void setPurchaseId(String id) {
    this.purchaseId = id;
  }

  /**
   * Custom getter for Date. Avoids expose representation bug and deals with nulls.
   *
   * @return Datetime.
   */
  public Timestamp getDatetime() {
    return this.datetime == null ? null : new Timestamp(this.datetime.getTime());
  }

  /**
   * Custom setter for Date. Avoids expose representation bug and deals with nulls.
   *
   * @param datetime Datetime.
   */
  public void setDatetime(Timestamp datetime) {
    this.datetime = datetime == null ? null : new Timestamp(datetime.getTime());
  }

  /**
   * Custom getter for Date. Avoids expose representation bug and deals with nulls.
   *
   * @return Updated datetime.
   */
  public Timestamp getUpdated() {
    return this.updated == null ? null : new Timestamp(this.updated.getTime());
  }

  /**
   * Custom setter for Date. Avoids expose representation bug and deals with nulls.
   *
   * @param updated Time that the purchase was updated.
   */
  public void setUpdated(Timestamp updated) {
    this.updated = updated == null ? null : new Timestamp(updated.getTime());
  }

  /**
   * Add orderLine.
   *
   * @param orderLine OrderLine.
   */
  public void addOrderLine(OrderLineEntity orderLine) {
    this.items.add(orderLine);
    orderLine.setPurchase(this);
  }
}
