package com.healthtracker.dao.impl;

import com.healthtracker.dao.ActivityDao;
import com.healthtracker.model.Activity;
import com.healthtracker.model.User;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ActivityDaoImpl implements ActivityDao {
    
    @Override
    public List<Activity> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Activity> query = session.createQuery(
                    "FROM Activity a WHERE a.user = :user ORDER BY a.timestamp DESC", Activity.class);
            query.setParameter("user", user);
            return query.list();
        }
    }

    @Override
    public void save(Activity entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Activity entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Activity entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public Activity findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Activity.class, id);
        }
    }

    @Override
    public List<Activity> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Activity ORDER BY timestamp DESC", Activity.class).list();
        }
    }
}
