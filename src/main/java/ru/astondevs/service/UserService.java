package ru.astondevs.service;

import ru.astondevs.dao.UserDao;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.model.UserModel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void saveUser(String name, String email, Integer age) {
        UserDTO userDTO = new UserDTO(UUID.randomUUID(), name, email, age);
        UserModel userModel = toModel(userDTO);
        userDao.save(userModel);
    }

    public List<UserDTO> getAllUsers() {
        return userDao.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(UUID id) {
        UserModel userModel = userDao.getById(id);
        if(userModel == null) {
            return null;
        }
        return toDTO(userModel);
    }

    public void updateUser(UUID currentUserId, String newName, String newEmail, int newAge) {
        UserModel userModel = userDao.getById(currentUserId);
        userModel.setName(newName);
        userModel.setEmail(newEmail);
        userModel.setAge(newAge);
        userDao.update(userModel);
    }

    public void deleteUser(UUID id) {
        userDao.delete(id);
    }

    private UserModel toModel(UserDTO userDTO) {
        UserModel userModel = new UserModel();
        userModel.setId(userDTO.getId());
        userModel.setName(userDTO.getName());
        userModel.setEmail(userDTO.getEmail());
        userModel.setAge(userDTO.getAge());
        userModel.setCreatedAt(Instant.now());
        return userModel;
    }

    private UserDTO toDTO(UserModel userModel) {
        return new UserDTO(
                userModel.getId(),
                userModel.getName(),
                userModel.getEmail(),
                userModel.getAge());
    }
}
