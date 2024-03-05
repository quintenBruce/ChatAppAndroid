package com.example.blankapplication.data.repositories;

import com.example.blankapplication.data.daos.MessageDao;
import com.example.blankapplication.data.models.Message;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class MessageLocalRepository {
    private MessageDao messageDao;

    public MessageLocalRepository(MessageDao messageDao) {
        this.messageDao = messageDao;
    }
    public Single<Message> findMessage(String id) {
        return messageDao.findMessageById(id);
    }
    public Single<List<Message>> getMessagesByConversation(String id) {
        return messageDao.getAllMessagesByConversationId(id);
    }

    public Single<Message> findMessageById(String id) {
        return messageDao.findMessageById(id);
    }

    public Completable insertMessages(List<Message> messages) {
        return messageDao.insertMessages(messages);
    }
    public Single<Boolean> existsById(String id) {
        return messageDao.existsById(id)
                .map(count -> count > 0);
    }
    public Single<List<String>> existsById(List<String> id) {
        return messageDao.existsById(id);
    }
    public Single<List<Message>> findMessages(List<String> ids) {
        return messageDao.findMessagesByIds(ids);
    }

}
