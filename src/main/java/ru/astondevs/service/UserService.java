package ru.astondevs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;
    @Value("${userservice.mail.created.subject}")
    private String userCreatedSubject;
    @Value("${userservice.mail.created.message}")
    private String userCreatedMessage;
    @Value("${userservice.mail.deleted.subject}")
    private String userDeletedSubject;
    @Value("${userservice.mail.deleted.message}")
    private String userDeletedMessage;



    public UserService(UserRepository userRepository, UserProducer userProducer) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
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
        UserEmailEvent userEmailEvent = UserEmailEvent.builder()
                .emailAddress(userDTO.getEmail())
                .subject(userCreatedSubject)
                .message(userCreatedMessage)
                .build();
        userProducer.send(userEmailEvent);
    }

    public void updateUser(UUID currentUserId, UserRequestDTO userRequestDTO) {
        UserModel userModel = userRepository.findById(currentUserId).orElseThrow();
        userModel.setName(userRequestDTO.getName());
        userModel.setEmail(userRequestDTO.getEmail());
        userModel.setAge(userRequestDTO.getAge());
        userRepository.save(userModel);
    }

    public void deleteUser(UUID id) {
        UserModel userModel = userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
        UserEmailEvent userEmailEvent = UserEmailEvent.builder()
                .emailAddress(userModel.getEmail())
                .subject(userDeletedSubject)
                .message(userDeletedMessage)
                .build();
        userProducer.send(userEmailEvent);
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
