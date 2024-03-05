package com.example.blankapplication.data.models;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.helpers.DateConverter;

import org.bson.types.ObjectId;

@Entity(tableName = "messages")
@TypeConverters(DateConverter.class)
public class Message {
    @PrimaryKey
    @NonNull
    private String id;
    private String content;
    private Date date;
    private String senderId;
    private String senderUserName;
    private String conversationId;
    private String formattedDate;

    public Message() {
        this.id = "";
    }

    public Message(@NonNull String id, String content, Date date, String senderId, String senderUserName, String conversationId) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.senderId = senderId;
        this.senderUserName = senderUserName;
        this.conversationId = conversationId;
    }

    public Message(String content, Date date, String senderId, String senderUserName, String conversationId) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.senderId = senderId;
        this.senderUserName = senderUserName;
        this.conversationId = conversationId;
    }
    public Message(MessageDTO messageDTO) {
        this.id = messageDTO.getId().toHexString();
        this.date = messageDTO.getDate();
        this.content = messageDTO.getContent();
        this.senderId = messageDTO.getSenderId().toHexString();
        this.conversationId = messageDTO.getConversationId().toHexString();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
