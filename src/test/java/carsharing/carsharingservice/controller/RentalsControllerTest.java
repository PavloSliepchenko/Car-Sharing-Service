package carsharing.carsharingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalsControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get rentals by user id and status")
    @WithMockUser(username = "User", authorities = "MANAGER")
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
    void getRentalByUserIdAndStatus_ValidRequest_ShouldReturnListOfDtos() throws Exception {
        Long userId = 2L;
        boolean isActive = false;
        MvcResult result = mockMvc.perform(get("/rentals?userId=" + userId
                        + "&isActive=" + isActive))
                .andExpect(status().isOk())
                .andReturn();

        List<RentalResponseDto> response =
                List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                        RentalResponseDto[].class));
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(1, response.get(0).getId());
        Assertions.assertEquals(2, response.get(0).getCar().getId());

        userId = 5L;
        isActive = true;
        result = mockMvc.perform(get("/rentals?userId=" + userId
                        + "&isActive=" + isActive))
                .andExpect(status().isOk())
                .andReturn();

        response = List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                RentalResponseDto[].class));
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(6, response.get(0).getId());
        Assertions.assertEquals(3, response.get(0).getCar().getId());
    }

    @Test
    @DisplayName("Get rentals by status")
    @WithMockUser(username = "User", authorities = "MANAGER")
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
    void getRentalsByStatus_ValidRequest_ShouldReturnListOfDtos() throws Exception {
        boolean isActive = false;
        MvcResult result = mockMvc.perform(get("/rentals/status?isActive=" + isActive))
                .andExpect(status().isOk())
                .andReturn();

        List<RentalResponseDto> response =
                List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                        RentalResponseDto[].class));
        Assertions.assertEquals(4, response.size());
        for (RentalResponseDto rental : response) {
            assertThat(rental.getId()).isIn(1L, 2L, 3L, 4L);
            assertThat(rental.getCar().getId()).isIn(1L, 2L, 3L, 5L);
        }

        isActive = true;
        result = mockMvc.perform(get("/rentals/status?isActive=" + isActive))
                .andExpect(status().isOk())
                .andReturn();

        response = List.of(objectMapper.readValue(result.getResponse().getContentAsString(),
                RentalResponseDto[].class));
        Assertions.assertEquals(2, response.size());
        for (RentalResponseDto rental : response) {
            assertThat(rental.getId()).isIn(5L, 6L);
            assertThat(rental.getCar().getId()).isIn(1L, 3L);
        }
    }

    @Test
    @WithUserDetails("fourth@user.com")
    @DisplayName("Get rental by rental id")
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
    void getRentalById_ValidRequest_ShouldReturnDto() throws Exception {
        Long rentalId = 3L;
        mockMvc.perform(get("/rentals/" + rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.rentalDate").value("13/01/2024"))
                .andExpect(jsonPath("$.actualReturnDate").value("14/01/2024"));

        rentalId = 5L;
        mockMvc.perform(get("/rentals/" + rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.rentalDate").value("13/01/2024"))
                .andExpect(jsonPath("$.car.id").value(1));
    }
}
