package com.healthtracker.dao.impl;

import com.healthtracker.dao.MeasurementDao;
import com.healthtracker.model.Measurement;
import com.healthtracker.model.User;
import com.healthtracker.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MeasurementDaoImpl implements MeasurementDao {

    @Override
    public List<Measurement> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Measurement> query = session.createQuery(
                    "FROM Measurement m WHERE m.user = :user", Measurement.class);
            query.setParameter("user", user);
            return query.list();
        }
    }

    @Override
    public void save(Measurement entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        }
    }

    @Override
    public void update(Measurement entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Override
    public void delete(Measurement entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        }
    }

    @Override
    public Measurement findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Measurement.class, id);
        }
    }

    @Override
    public List<Measurement> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Measurement", Measurement.class).list();
        }
    }
}
