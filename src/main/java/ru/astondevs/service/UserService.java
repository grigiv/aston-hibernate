package ru.astondevs.service;

import org.springframework.stereotype.Service;
import ru.astondevs.controller.requests.UserRequestDTO;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.model.UserModel;
import ru.astondevs.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    public void saveUser(UserRequestDTO userRequestDTO) {
        UserDTO userDTO = new UserDTO(UUID.randomUUID(),
                userRequestDTO.getName(),
                userRequestDTO.getEmail(),
                userRequestDTO.getAge());
        UserModel userModel = toModel(userDTO);
        userRepository.save(userModel);
    }

    public void updateUser(UUID currentUserId, UserRequestDTO userRequestDTO) {
        UserModel userModel = userRepository.findById(currentUserId).orElseThrow();
        userModel.setName(userRequestDTO.getName());
        userModel.setEmail(userRequestDTO.getEmail());
        userModel.setAge(userRequestDTO.getAge());
        userRepository.save(userModel);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
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
