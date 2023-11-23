package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import javax.management.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final SessionFactory sessionFactory = Util.getSessionFactory();

    public UserDaoHibernateImpl() {

    }


    @Override
    public void createUsersTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String sql = "CREATE TABLE IF NOT EXISTS users " +
                   "(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                   "name VARCHAR(25) NOT NULL, lastName VARCHAR(25) NOT NULL, " +
                   "age TINYINT NOT NULL)";

            NativeQuery query = session.createNativeQuery(sql);
            query.executeUpdate();
            transaction.commit();
            System.out.println("таблица успешно создана...");
        } catch (Exception e) {
            System.out.println("не удалось создать таблицу...");
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String sql = "DROP TABLE IF EXISTS users";
            NativeQuery query = session.createNativeQuery(sql);
            query.executeUpdate();
            transaction.commit();
            System.out.println("таблица успешно удалена...");
        } catch (Exception e) {
            System.out.println("не удалось удалить таблицу...");
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(new User(name, lastName, age));
            transaction.commit();
            System.out.println("User с именем – " + name + " добавлен в базу данных");
        } catch (Exception e) {
            System.out.println("не удалось добавить запись...");
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.load(User.class, id));
            transaction.commit();
            System.out.println("запись успешно удалена...");
        } catch (Exception e) {
            System.out.println("не удалось удалить запись...");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> rootEntry = cq.from(User.class);
            CriteriaQuery<User> all = cq.select(rootEntry);
            TypedQuery<User> allQuery = session.createQuery(all);
            usersList = allQuery.getResultList();
            usersList.forEach(user -> System.out.println(user.toString()));
        } catch ( Exception e) {
            System.out.println("не удалось прочитать таблицу...");
        }
        return usersList;
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            String sql = "DELETE FROM users";
            NativeQuery query = session.createNativeQuery(sql);
            query.executeUpdate();
            transaction.commit();
            System.out.println("таблица успешно очищена...");
        } catch (Exception e) {
            System.out.println("не удалось очистить таблицу...");
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
