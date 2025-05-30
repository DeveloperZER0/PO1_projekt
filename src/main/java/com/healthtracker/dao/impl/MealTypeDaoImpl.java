package com.healthtracker.dao.impl;

import com.healthtracker.dao.MealTypeDao;
import com.healthtracker.model.MealType;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MealTypeDaoImpl implements MealTypeDao {
    
    @Override
    public MealType findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MealType> query = session.createQuery(
                    "FROM MealType WHERE name = :name", MealType.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        }
    }

    @Override
    public List<MealType> findMainMeals() {
        return List.of();
    }

    @Override
    public void save(MealType entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(MealType entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(MealType entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public MealType findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(MealType.class, id);
        }
    }

    @Override
    public List<MealType> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM MealType ORDER BY name", MealType.class).list();
        }
    }
}