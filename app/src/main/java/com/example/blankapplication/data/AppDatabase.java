package com.example.blankapplication.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.blankapplication.data.daos.MessageDao;
import com.example.blankapplication.data.daos.UserDao;
import com.example.blankapplication.data.models.Message;
import com.example.blankapplication.data.models.User;

@Database(entities = {Message.class, User.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract UserDao userDao();
    // Migration from version 1 to version 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the 'User' table if it doesn't exist
            database.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT PRIMARY KEY, `name` TEXT, `userName` TEXT)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

}

