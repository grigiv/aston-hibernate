package ru.astondevs.dao;

import ru.astondevs.model.UserModel;

import java.util.List;
import java.util.UUID;


public interface UserDao {
    void save(UserModel userModel);

    UserModel getById(UUID id);

    List<UserModel> getAll();

    void update(UserModel userModel);

    void delete(UUID id);
}