package com.healthtracker.dao.impl;

import com.healthtracker.dao.GoalDao;
import com.healthtracker.model.Goal;
import com.healthtracker.model.User;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class GoalDaoImpl implements GoalDao {

    @Override
    public List<Goal> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Goal> query = session.createQuery(
                    "FROM Goal g WHERE g.user = :user", Goal.class);
            query.setParameter("user", user);
            return query.list();
        }
    }

    @Override
    public void save(Goal entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Goal entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Goal entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public Goal findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Goal.class, id);
        }
    }

    @Override
    public List<Goal> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Goal", Goal.class).list();
        }
    }
}
