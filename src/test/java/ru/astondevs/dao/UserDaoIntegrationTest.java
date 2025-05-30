package ru.astondevs.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import ru.astondevs.model.UserModel;
import ru.astondevs.util.HibernateUtil;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2")
            .withDatabaseName("userdb")
            .withUsername("postgres")
            .withPassword("postgres");

    static UserDao userDao;

    @BeforeAll
    static void setup() {
        postgres.start();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @BeforeEach
    void truncateTables() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Очистка всех таблиц в правильном порядке (если есть FK)
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();

            tx.commit();
        }
    }

    @Test
    void UserDao_saveUser_ShouldSave() {
        //Arrange
        UUID id = UUID.randomUUID();
        UserModel user = new UserModel();
        user.setId(id);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setAge(30);
        user.setCreatedAt(Instant.now());

        //Act
        userDao.save(user);
        UserModel retrieved = userDao.getById(id);

        //Assert
        assertNotNull(retrieved);
        assertEquals("Alice", retrieved.getName());
    }

    @Test
    void UserDao_getAllUsers_ShouldReturnAll() {
        //Arrange
        UUID id = UUID.randomUUID();
        UserModel user = new UserModel();
        user.setId(id);
        user.setName("Петя");
        user.setEmail("petya@example.com");
        user.setAge(20);
        user.setCreatedAt(Instant.now());
        userDao.save(user);

        //Act
        List<UserModel> users = userDao.getAll();

        //Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Петя", users.getFirst().getName());
    }

    @Test
    void UserDao_updateUser_ShouldUpdate() {
        //Arrange
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .name("Петя")
                .email("petya@example.com")
                .age(20)
                .createdAt(Instant.now())
                .build();
        userDao.save(userModel);
        String newName = "Вася";
        String newEmail = "vasya@example.com";
        int newAge = 40;
        UserModel updatedUserModel = UserModel.builder()
                .id(userModel.getId())
                .name(newName)
                .email(newEmail)
                .age(newAge)
                .createdAt(userModel.getCreatedAt())
                .build();
        //Act
        userDao.update(updatedUserModel);
        //Assert
        UserModel retrieved = userDao.getById(updatedUserModel.getId());
        assertEquals(newName, retrieved.getName());
        assertEquals(newEmail, retrieved.getEmail());
        assertEquals(newAge, retrieved.getAge());
    }

    @Test
    void UserDao_deleteUser_ShouldDelete() {
        //Arrange
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .name("Петя")
                .email("petya@example.com")
                .age(20)
                .createdAt(Instant.now())
                .build();
        userDao.save(userModel);

        //Act
        userDao.delete(userModel.getId());

        //Assert
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserModel user = session.get(UserModel.class, userModel.getId());
            assertNull(user);
        }
    }


}