package ru.astondevs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.model.UserModel;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
}
