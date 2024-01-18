package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Rental;
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
class RentalRepositoryTest {
    private static final boolean RETURNED = false;
    private static final boolean ACTIVE = true;
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Get rentals by user id and status")
    @Sql(scripts = {
            "classpath:database/users/add-four-users-to-db.sql",
            "classpath:database/cars/add-five-cars-to-db.sql",
            "classpath:database/rentals/add-six-rentals-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/clear-rentals-table.sql",
            "classpath:database/cars/clear-cars-table.sql",
            "classpath:database/users/clear-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndActive_ValidRequest_ShouldReturnListOfRentals() {
        List<Rental> actual = rentalRepository.findByUserIdAndActive(2L, RETURNED);
        int expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());

        actual = rentalRepository.findByUserIdAndActive(2L, ACTIVE);
        expectedSize = 0;
        Assert.assertEquals(expectedSize, actual.size());

        actual = rentalRepository.findByUserIdAndActive(5L, RETURNED);
        expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());

        actual = rentalRepository.findByUserIdAndActive(5L, ACTIVE);
        expectedSize = 1;
        Assert.assertEquals(expectedSize, actual.size());
    }

    @Test
    @DisplayName("Get all rentals by status")
    @Sql(scripts = {
            "classpath:database/users/add-four-users-to-db.sql",
            "classpath:database/cars/add-five-cars-to-db.sql",
            "classpath:database/rentals/add-six-rentals-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/clear-rentals-table.sql",
            "classpath:database/cars/clear-cars-table.sql",
            "classpath:database/users/clear-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByActive_ValidRequest_ShouldReturnListOfRentals() {
        List<Rental> actual = rentalRepository.findAllByActive(RETURNED);
        int expectedSize = 4;
        Assert.assertEquals(expectedSize, actual.size());

        actual = rentalRepository.findAllByActive(ACTIVE);
        expectedSize = 2;
        Assert.assertEquals(expectedSize, actual.size());
    }
}
