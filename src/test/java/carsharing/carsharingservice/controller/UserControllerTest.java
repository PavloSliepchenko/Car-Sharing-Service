package carsharing.carsharingservice.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
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
    @DisplayName("Get profile info")
    @WithUserDetails("third@user.com")
    @Sql(scripts = "classpath:database/users/add-four-users-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/clear-users-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUserInfo_ValidRequest_ShouldReturnDto() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("third@user.com"))
                .andExpect(jsonPath("$.firstName").value("Third"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    @DisplayName("Update user's role")
    @WithMockUser(username = "user", authorities = "MANAGER")
    @Sql(scripts = "classpath:database/users/add-four-users-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/clear-users-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRole_ValidRequest_ShouldReturnDto() throws Exception {
        RoleUpdateRequestDto requestDto = new RoleUpdateRequestDto("MANAGER");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Long userId = 4L;
        mockMvc.perform(put("/users/" + userId + "/role")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.email").value("fourth@user.com"));
    }

    @Test
    @DisplayName("Update profile info")
    @WithUserDetails("fifth@user.com")
    @Sql(scripts = "classpath:database/users/add-four-users-to-db.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/clear-users-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateProfileInfo_ValidRequest_ShouldReturnDto() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("newEmail@user.com");
        requestDto.setLastName("User");
        requestDto.setFirstName("Fifths");
        requestDto.setPassword("120584");
        requestDto.setRepeatPassword("120584");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").value(requestDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(requestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(requestDto.getLastName()));
    }
}
