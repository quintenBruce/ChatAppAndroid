package com.example.blankapplication.data.models;


import org.bson.types.ObjectId;

import java.util.List;

import lombok.NoArgsConstructor;


public class Conversation {
    private ObjectId id;
    private String name;
    private List<ObjectId> participants;
    private Integer unreadMessages;

    public Conversation() {

    }
    public Conversation(ObjectId Id, String Name, List<ObjectId> Participants) {
        this.id = Id;
        this.name = Name;
        this.participants = Participants;
        this.unreadMessages = 0;
    }
    public ObjectId getId() { return id; }
    public void setId(ObjectId Id) { this.id = Id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ObjectId> getParticipants() { return participants; }
    public void setParticipants(List<ObjectId> participants) { this.participants = participants; }

}
