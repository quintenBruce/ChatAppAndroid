package com.example.blankapplication.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.blankapplication.data.AppDatabase;
import com.example.blankapplication.data.models.User;
import com.example.blankapplication.data.repositories.UserLocalRepository;
import com.example.blankapplication.data.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserService extends Service {
    private UsersRepository userRemoteRepo;
    private UserLocalRepository userLocalRepo;
    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        this.userRemoteRepo = new UsersRepository();
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        userLocalRepo = new UserLocalRepository(appDatabase.userDao());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Close the database when the service is destroyed
        if (appDatabase != null) {
            appDatabase.close();
        }
    }

    public UserService(Context context) {
        this.userRemoteRepo = new UsersRepository();
        AppDatabase database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "message-db").addMigrations(AppDatabase.MIGRATION_1_2).build();
        userLocalRepo = new UserLocalRepository(database.userDao());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface GetUserCallback {
        void onSuccess(User user);

        void onFailure(Throwable e);
    }

    public void GetUser(String userId, GetUserCallback callback) {
        userLocalRepo.existsById(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@NonNull Boolean exists) {
                        if (!exists) {
                            getUserRemotely(userId, callback);
                        } else {
                            getUserLocally(userId, callback);
                        }
                        dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("SQLite", e.getMessage());
                        callback.onFailure(e);
                        dispose();
                    }
                });
    }

    private void insertUser(User user, GetUserCallback callback) {
        List<User> users = new ArrayList<>();
        users.add(user);
        userLocalRepo.insertUsers(users).subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("SQLite", "Successfully added user");
                        dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("SQLite", "Failed to add user");
                        dispose();
                    }
                });
        callback.onSuccess(user);
    }

    private void getUserRemotely(String userId, GetUserCallback callback) {
        Log.d("SQLite", "User does not exist");
        userRemoteRepo.getUser(userId, new GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                insertUser(user, callback);
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }

    private void getUserLocally(String userId, GetUserCallback callback) {
        Log.d("SQLite", "User exists");
        userLocalRepo.getUserById(userId).subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<User>() {
                    @Override
                    public void onSuccess(@NonNull User user) {
                        callback.onSuccess(user);
                        dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onFailure(e);
                        dispose();
                    }
                });
    }
}
