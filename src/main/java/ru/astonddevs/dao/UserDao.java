package ru.astonddevs.dao;

import ru.astonddevs.dto.UserDTO;

import java.util.List;
import java.util.UUID;


public interface UserDao {
    void save(UserDTO userDTO);

    UserDTO getById(UUID id);

    List<UserDTO> getAll();

    void update(UUID id, UserDTO userDTO);

    void delete(UUID id);
}