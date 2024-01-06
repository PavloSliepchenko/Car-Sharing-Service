package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT p "
            + "FROM Payment p "
            + "JOIN p.rental r "
            + "JOIN r.user u "
            + "WHERE u.id = :userId")
    List<Payment> getPaymentsByUserId(Long userId);

    List<Payment> findByRentalIdAndType(Long rentalId, Payment.Type type);

    Optional<Payment> findBySessionId(String sessionId);

    @Query(value = "SELECT p "
            + "FROM Payment p "
            + "JOIN p.rental r "
            + "JOIN r.user u "
            + "WHERE u.id = :userId AND p.status = :status")
    List<Payment> findByUserIdAndStatus(Long userId, Payment.Status status);
}
