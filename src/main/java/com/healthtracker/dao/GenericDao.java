package com.healthtracker.dao;

import java.util.List;

public interface GenericDao<T> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    T findById(Long id);
    List<T> findAll();
}
