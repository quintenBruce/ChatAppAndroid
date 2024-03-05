package com.example.blankapplication.data.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.blankapplication.data.models.Message;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY date DESC")
    Single<List<Message>> getAllMessagesByConversationId(String conversationId);

    @Query("SELECT * FROM messages WHERE id = :Id")
    public Single<Message> findMessageById(String Id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Completable insertMessages(List<Message> messages);
    @Query("SELECT COUNT(*) FROM messages WHERE id = :id")
    Single<Integer> existsById(String id);
    @Query("SELECT id FROM messages WHERE id IN (:ids)")
    Single<List<String>> existsById(List<String> ids);

    @Query("SELECT * FROM messages WHERE id IN (:ids)")
    Single<List<Message>> findMessagesByIds(List<String> ids);
}
