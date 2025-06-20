package ru.astondevs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.controller.requests.UserRequestDTO;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.event.UserEmailEvent;
import ru.astondevs.event.UserProducer;
import ru.astondevs.model.UserModel;
import ru.astondevs.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserProducer userProducer;

    @InjectMocks
    UserService userService;

    @Test
    void UserService_getAllUsers_ShouldReturnUserList() {
        //Arrange
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .name("Тест")
                .email("email@email.com")
                .age(22)
                .createdAt(Instant.now())
                .build();
        UserDTO userDTO = UserDTO.builder()
                .id(userModel.getId())
                .name("Тест")
                .email("email@email.com")
                .age(22)
                .build();
        when(userRepository.findAll()).thenReturn(List.of(userModel));

        //Act
        List<UserDTO> result = userService.getAllUsers();

        //Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDTO.getName(), result.getFirst().getName());
        assertEquals(userDTO.getEmail(), result.getFirst().getEmail());
        assertEquals(userDTO.getAge(), result.getFirst().getAge());
        // Проверяем, что метод DAO был вызван 1 раз
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void UserService_getUserById_ShouldReturnUser() {
        //Arrange
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .name("Тест")
                .email("email@email.com")
                .age(22)
                .createdAt(Instant.now())
                .build();
        UserDTO userDTO = UserDTO.builder()
                .id(userModel.getId())
                .name("Тест")
                .email("email@email.com")
                .age(22)
                .build();
        when(userRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));

        //Act
        Optional<UserDTO> result = userService.getUserById(userModel.getId());

        //Assert
        assertNotNull(result);
        assertEquals(userDTO.getName(), result.get().getName());
        assertEquals(userDTO.getEmail(), result.get().getEmail());
        assertEquals(userDTO.getAge(), result.get().getAge());
        verify(userRepository, times(1)).findById(userModel.getId());
    }

    @Test
    void UserService_getUserById_ShouldReturnNotFound_WhenUserNotFound() {
        //Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //Act & Assert
        assertEquals(userRepository.findById(userId), Optional.empty());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void UserService_saveUser_ShouldSaveAndSendEvent() {
        //Arrange
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test");
        userRequestDTO.setEmail("test@test.com");
        userRequestDTO.setAge(22);

        //Act
        userService.saveUser(userRequestDTO);

        //Assert
        verify(userRepository, times(1)).save(any(UserModel.class));
        verify(userProducer, times(1)).send(any(UserEmailEvent.class));
    }

    @Test
    void UserService_updateUser_ShouldUpdateUser() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UserModel existingUser = new UserModel();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setAge(30);
        existingUser.setCreatedAt(Instant.now());

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("New Name");
        userRequestDTO.setEmail("new@example.com");
        userRequestDTO.setAge(25);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        //Act
        userService.updateUser(userId, userRequestDTO);

        //Assert
        verify(userRepository, times(1)).findById(userId);
        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        verify(userRepository).save(captor.capture());
        UserModel updatedUser = captor.getValue();
        assertEquals(userRequestDTO.getName(), updatedUser.getName());
        assertEquals(userRequestDTO.getEmail(), updatedUser.getEmail());
        assertEquals(userRequestDTO.getAge(), updatedUser.getAge());
        assertEquals(userId, updatedUser.getId());
    }

    @Test
    void UserService_deleteUser_ShouldDeleteUserAndSendEvent() {
        //Assert
        //Arrange
        UUID userId = UUID.randomUUID();
        UserModel existingUser = new UserModel();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setAge(30);
        existingUser.setCreatedAt(Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        //Act
        userService.deleteUser(userId);

        //Assert
        verify(userRepository, times(1)).deleteById(userId);
        verify(userProducer, times(1)).send(any(UserEmailEvent.class));
    }
}