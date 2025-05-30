package com.healthtracker.dao.impl;

import com.healthtracker.dao.ActivityTypeDao;
import com.healthtracker.model.ActivityType;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ActivityTypeDaoImpl implements ActivityTypeDao {
    
    public ActivityType findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActivityType> query = session.createQuery(
                    "FROM ActivityType WHERE name = :name", ActivityType.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        }
    }
    
    public List<ActivityType> findByRequiresDistance(boolean requiresDistance) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ActivityType> query = session.createQuery(
                    "FROM ActivityType WHERE requiresDistance = :requiresDistance", ActivityType.class);
            query.setParameter("requiresDistance", requiresDistance);
            return query.list();
        }
    }

    @Override
    public void save(ActivityType entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(ActivityType entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(ActivityType entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public ActivityType findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ActivityType.class, id);
        }
    }

    @Override
    public List<ActivityType> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ActivityType ORDER BY name", ActivityType.class).list();
        }
    }
}