package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}

