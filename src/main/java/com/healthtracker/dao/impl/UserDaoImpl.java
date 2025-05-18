package com.healthtracker.dao.impl;

import com.healthtracker.dao.UserDao;
import com.healthtracker.model.Role;
import com.healthtracker.model.User;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "FROM User WHERE role = :role", User.class);
            query.setParameter("role", role);
            return query.list();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(u.id) FROM User u WHERE u.username = :username", Long.class);
            query.setParameter("username", username);
            return query.uniqueResult() > 0;
        }
    }

    @Override
    public void save(User entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(User entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(User entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }
}
