package purchasems.persistence.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import purchasems.persistence.domain.PurchaseEntity;

/** Spring JPA repository for Purchase entity. */
@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Integer> {

  PurchaseEntity findPurchaseEntityByPurchaseId(String purchaseId);

  // If orderId is null, this query returns a page of all Purchases persisted.
  @Query(
      "select p from PurchaseEntity p where "
          + "( (coalesce(:orderId) is null) or "
          + "(coalesce(:orderId) is not null and p.orderId = :orderId) ) ")
  List<PurchaseEntity> findByOrderIdPagination(@Param("orderId") String orderId, Pageable pageable);
}
