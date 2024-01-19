package carsharing.carsharingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
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
    @DisplayName("Get all cars")
    @Sql(scripts = "classpath:database/cars/add-five-cars-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/clear-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllCars_ValidRequest_ShouldReturnListOfDtos() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andReturn();
        List<CarResponseDto> carsList = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto[].class));
        for (CarResponseDto car : carsList) {
            assertThat(car.getId()).isIn(1L, 2L, 3L, 4L, 5L);
            assertThat(car.getBrand()).isIn("BMW", "VW", "RENAULT", "SKODA");
        }
    }

    @Test
    @DisplayName("Add a new car")
    @WithMockUser(username = "User", authorities = "MANAGER")
    @Sql(scripts = "classpath:database/cars/add-five-cars-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/clear-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addCar_ValidRequest_ShouldReturnDto() throws Exception {
        AddCarRequestDto requestDto = new AddCarRequestDto();
        requestDto.setBrand("CITROEN");
        requestDto.setModel("E-C4");
        requestDto.setType("SUV");
        requestDto.setInventory(5);
        requestDto.setDailyFee(BigDecimal.valueOf(23));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.model").value(requestDto.getModel()))
                .andExpect(jsonPath("$.brand").value(requestDto.getBrand()))
                .andExpect(jsonPath("$.type").value(requestDto.getType()))
                .andExpect(jsonPath("$.dailyFee").value(requestDto.getDailyFee()));
    }

    @Test
    @DisplayName("Get car by id")
    @WithMockUser(username = "User", authorities = "MANAGER")
    @Sql(scripts = "classpath:database/cars/add-five-cars-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/clear-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCarById_ValidRequest_ShouldReturnDto() throws Exception {
        mockMvc.perform(get("/cars/" + 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.model").value("B7"))
                .andExpect(jsonPath("$.brand").value("VW"))
                .andExpect(jsonPath("$.type").value("SEDAN"));
    }

    @Test
    @DisplayName("Update car")
    @WithMockUser(username = "User", authorities = "MANAGER")
    @Sql(scripts = "classpath:database/cars/add-five-cars-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/clear-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCar_ValidRequest_ShouldReturnDto() throws Exception {
        AddCarRequestDto requestDto = new AddCarRequestDto();
        requestDto.setModel("EXPRESS");
        requestDto.setBrand("RENAULT");
        requestDto.setType("UNIVERSAL");
        requestDto.setDailyFee(BigDecimal.valueOf(15));
        requestDto.setInventory(7);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(patch("/cars/" + 4L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.type").value(requestDto.getType()))
                .andExpect(jsonPath("$.brand").value(requestDto.getBrand()))
                .andExpect(jsonPath("$.model").value(requestDto.getModel()));
    }
}
