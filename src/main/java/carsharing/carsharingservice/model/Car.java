package carsharing.carsharingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@Table(name = "cars")
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE cars SET is_deleted = true WHERE id = ?")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @Enumerated(EnumType.STRING)
    private CarType type;
    @Column(nullable = false)
    private int inventory;
    private BigDecimal dailyFee;
    @Column(nullable = false)
    private boolean isDeleted = false;

    public enum CarType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
