package com.example.blankapplication.data.dtos;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MessageDTO {
    private ObjectId id;
    private String content;
    private Date date;
    private ObjectId senderId;
    private ObjectId conversationId;

    public MessageDTO() {}

    public MessageDTO(String content, Date date, ObjectId sender, ObjectId conversationId) {
        this.content = content;
        this.date = date;
        this.senderId = sender;
        this.conversationId = conversationId;
    }
    public String toJson() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String isoDateString = isoFormat.format(date);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);
            jsonObject.put("conversationId", conversationId.toHexString());
            jsonObject.put("senderId", senderId.toHexString());
            jsonObject.put("date", isoDateString);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ObjectId getId() { return id; }

    public void setId(ObjectId id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public ObjectId getSenderId() { return senderId; }

    public void setSenderId(ObjectId senderId) { this.senderId = senderId; }

    public ObjectId getConversationId() { return conversationId; }

    public void setConversationId(ObjectId conversationId) { this.conversationId = conversationId; }
}

