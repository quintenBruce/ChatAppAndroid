package com.example.blankapplication.data.dtos;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConversationDTO {
    //
    //need to create ConversationDTO then create an object in conversationviewmodel
    //
    private String id;
    private String name;
    private List<String> participants;

    public ConversationDTO() {

    }
    public ConversationDTO(String Id, String Name, List<String> Participants) {
        this.id = Id;
        this.name = Name;
        this.participants = Participants;
    }
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("participants", participants);
            JSONArray participantsArray = new JSONArray(participants);
            jsonObject.put("participants", participantsArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getId() { return id; }
    public void setId(String Id) { this.id = Id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
    public void addParticipant(String _id) {
        if (this.participants == null) {
            participants = new ArrayList<>();
        }
        participants.add(_id);
    }
}
