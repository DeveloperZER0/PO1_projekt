package com.healthtracker.dao.impl;

import com.healthtracker.dao.MealDao;
import com.healthtracker.model.Meal;
import com.healthtracker.model.User;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MealDaoImpl implements MealDao {

    @Override
    public List<Meal> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Meal> query = session.createQuery(
                    "FROM Meal m WHERE m.user = :user", Meal.class);
            query.setParameter("user", user);
            return query.list();
        }
    }

    @Override
    public void save(Meal entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Meal entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Meal entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public Meal findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Meal.class, id);
        }
    }

    @Override
    public List<Meal> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Meal", Meal.class).list();
        }
    }
}
