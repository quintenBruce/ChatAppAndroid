package com.example.blankapplication.helpers;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.blankapplication.data.AppDatabase;
import com.example.blankapplication.data.models.Message;
import com.example.blankapplication.data.models.User;
import com.example.blankapplication.data.repositories.MessageLocalRepository;
import com.example.blankapplication.services.UserService;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MessageProcessor {
    private MessageLocalRepository messageLocalRepo;
    private UserService userService;
    public MessageProcessor(Context context) {
        AppDatabase database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "message-db").addMigrations(AppDatabase.MIGRATION_1_2).build();
        messageLocalRepo = new MessageLocalRepository(database.messageDao());
        this.userService = new UserService(context);
    }
    public void processAndStore(List<Message> messages) {
        process(messages, new ProcessMessagesCallback() {
            @Override
            public void onSuccess(List<Message>updatedMessages) {
                store(updatedMessages);
            }

            @Override
            public void onFailure() {

            }
        });
    }
    public void processAndStore(List<Message> messages, ProcessMessagesCallback callback) {
        process(messages, new ProcessMessagesCallback() {
            @Override
            public void onSuccess(List<Message>updatedMessages) {
                callback.onSuccess(updatedMessages);
                store(updatedMessages);
            }

            @Override
            public void onFailure() {

            }
        });
    }
    private void setFields(List<Message> messages, Dictionary<String, User> dict) {
        for (Message message: messages) {
            String senderUserName = dict.get(message.getSenderId()).getUserName();
            message.setSenderUserName(senderUserName);

            Date date = message.getDate();
            Instant instant = date.toInstant();
            ZoneId systemZone = ZoneId.systemDefault();
            ZoneOffset offset = systemZone.getRules().getOffset(instant);
            OffsetDateTime timeWithOffset = instant.atOffset(offset);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
            String formattedTime = timeWithOffset.format(formatter);

            message.setFormattedDate(formattedTime);
        }
    }
    public interface ProcessMessagesCallback {
        void onSuccess(List<Message> messages);
        void onFailure();
    }
    public void process(List<Message> messages, ProcessMessagesCallback callback) {
        List<String> senderIds = new ArrayList<>();
        Dictionary<String, User> senders = new Hashtable<>();
        for (Message message: messages) {
            if (!senderIds.contains(message.getSenderId()))
                senders.put(message.getSenderId(), new User());
        }
        //senders contains distinct user ids
        //fetch the corresponding user and store it in dictionary
        Enumeration<String> keys = senders.keys();
        AtomicInteger completedOperations = new AtomicInteger(0);
        Integer nUsers = senders.size();

        while (keys.hasMoreElements()) {
            String userId = keys.nextElement();
            userService.GetUser(userId, new UserService.GetUserCallback() {
                @Override
                public void onSuccess(User user) {
                    senders.remove(userId);
                    senders.put(userId, user);
                    completedOperations.incrementAndGet();
                    if (completedOperations.get() == nUsers) {
                        setFields(messages, senders);
                        store(messages);
                        callback.onSuccess(messages);
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e("SQLite", "Catostrophic failure");
                }
            });
        }
    }

    private void store(List<Message> messages) {
        messageLocalRepo.insertMessages(messages).subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("SQLite", "Successfully added messages");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("SQLite", "Failure adding messages");
                    }
                });
    }

}
