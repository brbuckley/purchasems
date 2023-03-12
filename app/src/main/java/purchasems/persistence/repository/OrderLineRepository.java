package purchasems.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import purchasems.persistence.domain.OrderLineEntity;

/** Spring JPA repository for OrderLine entity. */
@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineEntity, Integer> {}
