package purchasems.api.model.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.sql.Timestamp;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import purchasems.api.model.orderline.OrderLineResponse;
import purchasems.api.model.supplier.SupplierResponse;

/** Purchase Response Model. */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "order_id",
  "supplier_order_id",
  "supplier",
  "items",
  "status",
  "datetime",
  "updated"
})
public class PurchaseResponse {

  private String id;

  @JsonProperty("order_id")
  private String orderId;

  @JsonProperty("supplier_order_id")
  private String supplierOrderId;

  private SupplierResponse supplier;
  private String status;

  @JsonFormat(pattern = "MM-dd-yyyy'T'HH:mm:ss.SSS'Z'")
  private Timestamp datetime;

  @JsonFormat(pattern = "MM-dd-yyyy'T'HH:mm:ss.SSS'Z'")
  private Timestamp updated;

  private List<OrderLineResponse> items;

  /**
   * Custom Constructor.
   *
   * @param id Id.
   * @param orderId Order id.
   * @param supplier Supplier Response.
   * @param status Purchase status name.
   * @param datetime Timestamp.
   * @param updated Time when the purchase was updated.
   * @param items List of OrderLines.
   * @param supplierOrderId Supplier Order id. It confirms that the purchase was made at the
   *     supplier.
   */
  public PurchaseResponse(
      String id,
      String orderId,
      SupplierResponse supplier,
      String status,
      Timestamp datetime,
      Timestamp updated,
      List<OrderLineResponse> items,
      String supplierOrderId) {
    this.id = id;
    this.orderId = orderId;
    this.supplier = supplier;
    this.status = status;
    this.datetime = datetime == null ? null : new Timestamp(datetime.getTime());
    this.updated = updated == null ? null : new Timestamp(updated.getTime());
    this.items = items;
    this.supplierOrderId = supplierOrderId;
  }

  public Timestamp getDatetime() {
    return this.datetime == null ? null : new Timestamp(this.datetime.getTime());
  }

  public void setDatetime(Timestamp datetime) {
    this.datetime = datetime == null ? null : new Timestamp(datetime.getTime());
  }

  public Timestamp getUpdated() {
    return this.updated == null ? null : new Timestamp(this.updated.getTime());
  }

  public void setUpdated(Timestamp updated) {
    this.updated = updated == null ? null : new Timestamp(updated.getTime());
  }
}
