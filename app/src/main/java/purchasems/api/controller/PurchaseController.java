package purchasems.api.controller;

import java.util.List;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import purchasems.api.model.purchase.PurchaseResponse;
import purchasems.exception.NotExistException;
import purchasems.service.PurchaseService;
import purchasems.util.HeadersUtil;

/** Controller for the purchase services. */
@AllArgsConstructor
@RestController
@Validated
public class PurchaseController {

  private final PurchaseService purchaseService;

  /**
   * Get a purchase by id.
   *
   * @param correlationId Correlation id Header.
   * @param purchaseId Purchase id.
   * @return Purchase Response.
   * @throws NotExistException Not Exist Exception.
   */
  @GetMapping(value = "/{purchaseId}", produces = "application/json")
  public ResponseEntity<PurchaseResponse> getPurchase(
      @RequestHeader(name = "X-Correlation-Id", required = false) String correlationId,
      @Pattern(regexp = "^PUR[0-9]{7}$") @PathVariable("purchaseId") String purchaseId)
      throws NotExistException {
    return ResponseEntity.ok()
        .headers(HeadersUtil.defaultHeaders(correlationId))
        .body(purchaseService.getPurchase(purchaseId));
  }

  /**
   * Get all purchases with filters.
   *
   * @param correlationId Correlation id header.
   * @param orderId Order id filter.
   * @param sort Sort by.
   * @param limit Limit results.
   * @param offset Offset pagination.
   * @return List of Purchases.
   * @throws NotExistException Not Exist Exception.
   */
  @GetMapping(value = "/", produces = "application/json")
  public ResponseEntity<List<PurchaseResponse>> getPurchases(
      @RequestHeader(name = "X-Correlation-Id", required = false) String correlationId,
      @Pattern(regexp = "^(ORD[0-9]{7}(|,))*$") @RequestParam(name = "order-id", required = false)
          String orderId,
      @Pattern(regexp = "^(asc|desc)\\.(status|datetime)$")
          @RequestParam(name = "sort", defaultValue = "asc.datetime", required = false)
          String sort,
      @RequestParam(name = "limit", defaultValue = "50", required = false) int limit,
      @RequestParam(name = "offset", defaultValue = "0", required = false) int offset)
      throws NotExistException {
    return ResponseEntity.ok()
        .headers(HeadersUtil.defaultHeaders(correlationId))
        .body(purchaseService.getPurchases(orderId, sort, limit, offset));
  }
}
