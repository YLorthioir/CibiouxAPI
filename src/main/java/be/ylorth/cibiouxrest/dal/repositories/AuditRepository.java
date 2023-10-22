package be.ylorth.cibiouxrest.dal.repositories;

import be.ylorth.cibiouxrest.dal.models.ReservationAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<ReservationAudit, Long> {
}
