package dao;

import java.util.List;

public interface GenericDao<T> {
    void save(T t);
    void update(T t);
    void delete(T t);
    T findById(long id);
    List<T> findAll();
}
