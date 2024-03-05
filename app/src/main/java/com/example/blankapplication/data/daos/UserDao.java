package com.example.blankapplication.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.blankapplication.data.models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    Single<List<User>> getAllUsers();
    @Query("SELECT COUNT(*) FROM users WHERE id = :id")
    Single<Integer> existsById(String id);
    @Query("SELECT userName FROM users WHERE id = :id")
    Single<String> getUsernameById(String id);
    @Query("SELECT name FROM users WHERE id = :id")
    Single<String> getNameById(String id);
    @Query("SELECT * FROM users WHERE id = :id")
    Single<User> getUserById(String id);
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insertUsers(List<User> users);
}
