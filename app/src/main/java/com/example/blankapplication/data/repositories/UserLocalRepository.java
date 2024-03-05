package com.example.blankapplication.data.repositories;

import com.example.blankapplication.data.daos.UserDao;
import com.example.blankapplication.data.models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserLocalRepository {
    private UserDao userDao;

    public UserLocalRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public Single<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public Single<Boolean> existsById(String id) {
        return userDao.existsById(id)
                .map(count -> count > 0);
    }

    public Single<String> getUsernameById(String id) {
        return userDao.getUsernameById(id);
    }

    public Single<String> getNameById(String id) {
        return userDao.getNameById(id);
    }

    public Completable insertUsers(List<User> users) {
        return userDao.insertUsers(users);
    }
    public Single<User> getUserById(String id) {
        return userDao.getUserById(id);
    }
}
