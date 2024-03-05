package com.example.blankapplication.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.blankapplication.data.AppDatabase;
import com.example.blankapplication.data.models.Message;
import com.example.blankapplication.data.repositories.MessageLocalRepository;
import com.example.blankapplication.data.repositories.MessageRepository;
import com.example.blankapplication.helpers.MessageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MessageService extends Service {
    private MessageRepository messageRemoteRepo;
    private MessageLocalRepository messageLocalRepo;
    private MessageProcessor messageProcessor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MessageService(Context context) {
        this.messageRemoteRepo = new MessageRepository();
        AppDatabase database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "message-db").addMigrations(AppDatabase.MIGRATION_1_2).build();
        messageLocalRepo = new MessageLocalRepository(database.messageDao());
        this.messageProcessor = new MessageProcessor(context);
    }

    public interface GetMessageCallback {
        void onSuccess(Message message);

        void onFailure(Throwable e);
    }

    public interface GetMessageIdsCallback {
        void onSuccess(List<String> ids);

        void onFailure(Throwable e);
    }

    public interface GetMessagesCallback {
        void onSuccess(List<Message> messages);

        void onFailure(Throwable e);
    }

    public void getMessage(String id, GetMessageCallback callback) {

    }

    public void getMessageIdsByConversation(String id, GetMessageIdsCallback callback) {
        messageRemoteRepo.getMessagesIdsByConversation(id, callback);
    }

    public void getMessages(List<String> ids, GetMessagesCallback callback) {
        List<String> notLocal = ids;
        List<Message> messages = new ArrayList<>();
        AtomicInteger completedOperations = new AtomicInteger(0);


        messageLocalRepo.existsById(ids).subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(@NonNull List<String> localIds) {
                        notLocal.removeAll(localIds);
                        //get local messages
                        if (localIds.isEmpty()) {
                            completedOperations.incrementAndGet();
                            if (completedOperations.get() == 2)
                                callback.onSuccess(messages);
                        } else {
                            messageLocalRepo.findMessages(localIds).subscribeOn(Schedulers.io())
                                    .subscribe(new DisposableSingleObserver<List<Message>>() {
                                        @Override
                                        public void onSuccess(@NonNull List<Message> localMessages) {
                                            messages.addAll(localMessages);
                                            completedOperations.incrementAndGet();
                                            if (completedOperations.get() == 2)
                                                callback.onSuccess(messages);
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {

                                        }
                                    });
                        }
                        if (notLocal.isEmpty()) {
                            completedOperations.incrementAndGet();
                            if (completedOperations.get() == 2)
                                callback.onSuccess(messages);
                        } else {
                            messageRemoteRepo.getMessages(notLocal, new GetMessagesCallback() {
                                @Override
                                public void onSuccess(List<Message> remoteMessages) {
                                    messageProcessor.processAndStore(remoteMessages, new MessageProcessor.ProcessMessagesCallback() {
                                        @Override
                                        public void onSuccess(List<Message> updatedMessages) {
                                            messages.addAll(updatedMessages);
                                            completedOperations.incrementAndGet();
                                            if (completedOperations.get() == 2)
                                                callback.onSuccess(messages);
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });


    }
    private void getLocalMessages(List<Message> messages, List<String> ids) {

    }

}
