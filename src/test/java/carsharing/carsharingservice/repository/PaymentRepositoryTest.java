package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Payment;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Get payments by user id")
    @Sql(scripts = {
            "classpath:database/users/add-four-users-to-db.sql",
            "classpath:database/cars/add-five-cars-to-db.sql",
            "classpath:database/rentals/add-six-rentals-to-db.sql",
            "classpath:database/payments/add-six-payments-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/clear-payments-table.sql",
            "classpath:database/rentals/clear-rentals-table.sql",
            "classpath:database/cars/clear-cars-table.sql",
            "classpath:database/users/clear-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPaymentsByUserId_ValidRequest_ShouldReturnListOfPayments() {
        List<Payment> actual = paymentRepository.getPaymentsByUserId(2L);
        int expectedSize = 2;
        Assert.assertEquals(expectedSize, actual.size());

        actual = paymentRepository.getPaymentsByUserId(3L);
        expectedSize = 2;
        Assert.assertEquals(expectedSize, actual.size());

        actual = paymentRepository.getPaymentsByUserId(5L);
        expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());
    }

    @Test
    @DisplayName("Find payments by user id and a payment status")
    @Sql(scripts = {
            "classpath:database/users/add-four-users-to-db.sql",
            "classpath:database/cars/add-five-cars-to-db.sql",
            "classpath:database/rentals/add-six-rentals-to-db.sql",
            "classpath:database/payments/add-six-payments-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/clear-payments-table.sql",
            "classpath:database/rentals/clear-rentals-table.sql",
            "classpath:database/cars/clear-cars-table.sql",
            "classpath:database/users/clear-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndStatus_ValidRequest_ShouldReturnListOfPayments() {
        Payment.Status paid = Payment.Status.PAID;
        Payment.Status pending = Payment.Status.PENDING;

        List<Payment> actual = paymentRepository.findByUserIdAndStatus(2L, paid);
        int expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());

        actual = paymentRepository.findByUserIdAndStatus(2L, pending);
        expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());

        actual = paymentRepository.findByUserIdAndStatus(5L, paid);
        expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());

        actual = paymentRepository.findByUserIdAndStatus(5L, pending);
        expectedSize = 0;
        Assert.assertEquals(expectedSize, actual.size());
    }
}
