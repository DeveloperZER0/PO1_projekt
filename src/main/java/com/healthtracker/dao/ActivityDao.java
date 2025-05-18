package com.healthtracker.dao;

import com.healthtracker.model.Activity;
import com.healthtracker.model.User;

import java.util.List;

public interface ActivityDao extends GenericDao<Activity>{
    // DAO dla aktywności fizycznej (Activity).
    // Powinno umożliwiać dodawanie, usuwanie i pobieranie aktywności użytkownika.
    List<Activity> findByUser(User user);
}
