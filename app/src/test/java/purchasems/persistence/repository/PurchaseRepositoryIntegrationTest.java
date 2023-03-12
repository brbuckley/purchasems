package purchasems.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import purchasems.api.model.purchase.PurchaseStatus;
import purchasems.persistence.domain.OrderLineEntity;
import purchasems.persistence.domain.PurchaseEntity;

@DataJpaTest
public class PurchaseRepositoryIntegrationTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private PurchaseRepository purchaseRepository;

  @Test
  public void testFindPurchaseEntityByPurchaseId_whenPurchaseExists_thenReturnPurchase() {
    PurchaseEntity found = purchaseRepository.findPurchaseEntityByPurchaseId("PUR0000001");

    assertEquals("ORD0000001", found.getOrderId());
  }

  @Test
  public void testFindByOrderIdPagination_whenNoParams_thenReturnPurchases() {
    Pageable pageable =
        PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "datetime")));

    List<PurchaseEntity> found = purchaseRepository.findByOrderIdPagination(null, pageable);

    // There should be 1 purchase saved
    assertEquals(1, found.size());
  }

  @Test
  public void testFindByOrderIdPagination_whenOrderId_thenReturnPurchases() {
    // Persists purchase for ORD0000004
    persistPurchaseUtil();

    Pageable pageable =
        PageRequest.of(0, 50, Sort.by(new Sort.Order(Sort.Direction.ASC, "datetime")));

    List<PurchaseEntity> found = purchaseRepository.findByOrderIdPagination("ORD0000004", pageable);

    assertEquals("ORD0000004", found.get(0).getOrderId());
  }

  private void persistPurchaseUtil() {
    // Insert Test Purchase
    PurchaseEntity entity = new PurchaseEntity();
    entity.setDatetime(Timestamp.valueOf("2007-09-23 10:10:10.0"));
    entity.setStatus(PurchaseStatus.ORDERED);
    entity.setOrderId("ORD0000004");
    entityManager.persist(entity);

    OrderLineEntity orderLine = new OrderLineEntity();
    orderLine.setProductId(4);
    orderLine.setPrice(new BigDecimal("10"));
    orderLine.setQuantity(1);

    entity.addOrderLine(orderLine);
    entityManager.persist(orderLine);
  }
}
