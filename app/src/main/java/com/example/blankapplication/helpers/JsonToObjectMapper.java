package com.example.blankapplication.helpers;

import com.example.blankapplication.data.models.Conversation;
import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.data.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonToObjectMapper {
    private static ObjectMapper mapper = new ObjectMapper();

    public static List<Conversation> JsonToConversation(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<List<Conversation>>(){});
    }
    public static List<MessageDTO> JsonToMessages(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<List<MessageDTO>>(){});
    }
    public static MessageDTO JsonToMessage(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<MessageDTO>(){});
    }
    public static User JsonToUser(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<User>(){});
    }
    public static String MessageToJson(MessageDTO message) throws JsonProcessingException {
        return mapper.writeValueAsString(message);
    }
    public static List<String> JsonToString(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<List<String>>() {});
    }
    public static String StringToJson(List<String> strings) throws JsonProcessingException {
        return mapper.writeValueAsString(strings);
    }


}
