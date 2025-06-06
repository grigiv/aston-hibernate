package ru.astondevs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import ru.astondevs.controller.requests.UserRequestDTO;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.model.UserModel;
import ru.astondevs.repository.UserRepository;
import ru.astondevs.service.UserService;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2")
            .withDatabaseName("userdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        postgres.start();
    }

    @BeforeEach
    void truncateTables() {
        userRepository.deleteAll();
    }

    @Test
    void UserController_getAllUsers_ShouldReturnAll() throws Exception {
        //Arrange
        UUID id = UUID.randomUUID();
        UserModel user = new UserModel();
        user.setId(id);
        user.setName("Петя");
        user.setEmail("petya@example.com");
        user.setAge(20);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);

        //Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Петя"));
    }

    @Test
    void UserController_getUserById_ShouldReturnUser() throws Exception {
        //Arrange
        UUID id = UUID.randomUUID();
        UserModel user = new UserModel();
        user.setId(id);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setAge(30);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void UserController_getUserById_ShouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void UserController_createUser_ShouldCreateUser() throws Exception {
        //Arrange
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("New Name");
        userRequestDTO.setEmail("new@example.com");
        userRequestDTO.setAge(25);

        //Act
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated());

        //Assert
        UserModel userModel = userRepository.findAll().getFirst();
        assertEquals(userRequestDTO.getName(), userModel.getName());
    }

    @Test
    void UserController_updateUser_ShouldUpdate() throws Exception {
        //Arrange
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .name("Петя")
                .email("petya@example.com")
                .age(20)
                .createdAt(Instant.now())
                .build();
        userRepository.save(userModel);
        String newName = "Вася";
        String newEmail = "vasya@example.com";
        int newAge = 40;
        UserDTO updatedUserDTO = new UserDTO(userModel.getId(), newName, newEmail, newAge);

        //Act
       mockMvc.perform(put("/users/" + userModel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDTO)))
                .andExpect(status().isOk());

        //Assert
        UserModel updatedUserModel = userRepository.findAll().getFirst();
        assertEquals(updatedUserDTO.getName(), updatedUserModel.getName());
    }

    @Test
    void UserDao_deleteUser_ShouldDelete() throws Exception {
        //Arrange
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .name("Петя")
                .email("petya@example.com")
                .age(20)
                .createdAt(Instant.now())
                .build();
        userRepository.save(userModel);

        // Act
        mockMvc.perform(delete("/users/" + userModel.getId()))
                .andExpect(status().isNoContent());

        // Assert
        assertEquals(0, userRepository.count());
        assertTrue(userRepository.findById(userModel.getId()).isEmpty());
    }


}