package ru.astondevs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.dao.UserDao;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.model.UserModel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserDao userDao;

    @InjectMocks
    UserService userService;

    @Test
    void UserService_saveUser_ShouldSave() {
        //Arrange
        //Act
        userService.saveUser("Test", "test@test.ru", 22);

        //Assert
        verify(userDao, times(1)).save(any(UserModel.class));
    }

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
                .id(UUID.randomUUID())
                .name("Тест")
                .email("email@email.com")
                .age(22)
                .build();
        when(userDao.getAll()).thenReturn(List.of(userModel));

        //Act
        List<UserDTO> result = userService.getAllUsers();

        //Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDTO.getName(), result.getFirst().getName());
        assertEquals(userDTO.getEmail(), result.getFirst().getEmail());
        assertEquals(userDTO.getAge(), result.getFirst().getAge());
        // Проверяем, что метод DAO был вызван 1 раз
        verify(userDao, times(1)).getAll();
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
        when(userDao.getById(userModel.getId())).thenReturn(userModel);

        //Act
        UserDTO result = userService.getUserById(userModel.getId());

        //Assert
        assertNotNull(result);
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getAge(), result.getAge());
        verify(userDao, times(1)).getById(userModel.getId());
    }

    @Test
    void UserService_getUserById_ShouldReturnNull_WhenUserNotFound() {
        //Arrange
        UUID userId = UUID.randomUUID();
        when(userDao.getById(userId)).thenReturn(null);
        //Act
        UserDTO result = userService.getUserById(userId);
        //Assert
        assertNull(result);
        verify(userDao, times(1)).getById(userId);
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

        String newName = "New Name";
        String newEmail = "new@example.com";
        int newAge = 25;

        when(userDao.getById(userId)).thenReturn(existingUser);

        //Act
        userService.updateUser(userId, newName, newEmail, newAge);

        //Assert
        verify(userDao, times(1)).getById(userId);
        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        verify(userDao).update(captor.capture());
        UserModel updatedUser = captor.getValue();
        assertEquals(newName, updatedUser.getName());
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(newAge, updatedUser.getAge());
        assertEquals(userId, updatedUser.getId());
    }

    @Test
    void UserService_deleteUser_ShouldDeleteUser() {
        //Assert
        UUID userId = UUID.randomUUID();

        //Act
        userService.deleteUser(userId);

        //Assert
        verify(userDao, times(1)).delete(userId);
    }
}