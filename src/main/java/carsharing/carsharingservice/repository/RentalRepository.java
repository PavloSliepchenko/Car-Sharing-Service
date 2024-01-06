package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Rental;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query(value = "SELECT r "
            + "FROM Rental r "
            + "JOIN r.user u "
            + "WHERE u.id = :userId AND r.isActive = :active")
    List<Rental> findByUserIdAndActive(Long userId, boolean active);

    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    @Query(value = "SELECT r "
            + "FROM Rental r "
            + "WHERE r.isActive = :active")
    List<Rental> findAllByActive(boolean active);

    List<Rental> findByUserId(Long userId);
}
