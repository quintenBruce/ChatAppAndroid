package com.example.blankapplication.data.models;

import com.example.blankapplication.data.dtos.MessageDTO;

import java.util.Date;

public class MessageRecycler {
    private String id;
    private String content;
    private Date date;
    private String senderUserName;
    private String formattedDate;
    private boolean isSender;

    public MessageRecycler(){}
    public MessageRecycler(String id, String content, Date date, String senderUserName, boolean isSender) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.senderUserName = senderUserName;
        this.isSender = isSender;
    }
    public MessageRecycler(MessageDTO messageDTO) {
        this.id = messageDTO.getId().toHexString();
        this.content = messageDTO.getContent();
        this.date = messageDTO.getDate();
        this.senderUserName = null;
        this.isSender = false;
    }
    public MessageRecycler(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.date = message.getDate();
        this.formattedDate = message.getFormattedDate();
        this.senderUserName = message.getSenderUserName();
        this.isSender = false;
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

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }
    public String getFormattedDate() {
        return this.formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
