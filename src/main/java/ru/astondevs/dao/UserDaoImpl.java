package ru.astondevs.dao;

import org.hibernate.SessionFactory;
import ru.astondevs.model.UserModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(UserModel userModel) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(userModel);
            tx.commit();
        } catch (Exception e) {
            logger.error("Произошла ошибка при создании пользователя", e);
        }
    }

    @Override
    public UserModel getById(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(UserModel.class, id);
        }
    }

    @Override
    public List<UserModel> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from UserModel", UserModel.class).stream()
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void update(UserModel updatedUserModel) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(updatedUserModel);
            tx.commit();
        } catch (Exception e) {
            logger.error("Произошла ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            UserModel userModel = session.get(UserModel.class, id);
            if (userModel != null) session.remove(userModel);
            tx.commit();
        } catch (Exception e) {
            logger.error("Произошла ошибка при удалении пользователя", e);
        }
    }
}
