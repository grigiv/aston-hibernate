package ru.astonddevs.dao;

import ru.astonddevs.dto.UserDTO;
import ru.astonddevs.model.UserModel;
import ru.astonddevs.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public void save(UserDTO userDTO) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            UserModel userModel = toModel(userDTO);
            session.persist(userModel);
            tx.commit();
        } catch (Exception e) {
            logger.error("Произошла ошибка при создании пользователя", e);
        }
    }

    @Override
    public UserDTO getById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserModel userModel = session.get(UserModel.class, id);
            return toDTO(userModel);
        }
    }

    @Override
    public List<UserDTO> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from UserModel", UserModel.class).stream()
                    .map(this::toDTO).
                    collect(Collectors.toList());
        }
    }

    @Override
    public void update(UUID id, UserDTO updatedUserDTO) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            UserModel userModel = session.get(UserModel.class, id);
            if (userModel != null) {
                userModel.setName(updatedUserDTO.getName());
                userModel.setEmail(updatedUserDTO.getEmail());
                userModel.setAge(updatedUserDTO.getAge());
                session.persist(userModel);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Произошла ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            UserModel userModel = session.get(UserModel.class, id);
            if (userModel != null) session.remove(userModel);
            tx.commit();
        } catch (Exception e) {
            logger.error("Произошла ошибка при удалении пользователя", e);
        }
    }

    private UserModel toModel(UserDTO userDTO) {
        UserModel userModel = new UserModel();
        userModel.setId(UUID.randomUUID());
        userModel.setName(userDTO.getName());
        userModel.setEmail(userDTO.getEmail());
        userModel.setAge(userDTO.getAge());
        userModel.setCreatedAt(Instant.now());
        return userModel;
    }

    private UserDTO toDTO(UserModel userModel) {
        return new UserDTO(
                userModel.getName(),
                userModel.getEmail(),
                userModel.getAge());
    }
}
