package com.example.blankapplication.data.repositories;

import androidx.annotation.NonNull;

import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.data.models.Message;
import com.example.blankapplication.data.remote.OkHTTP;
import com.example.blankapplication.helpers.JsonToObjectMapper;
import com.example.blankapplication.services.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageRepository {
    private OkHTTP remote;

    public MessageRepository() {
        remote = new OkHTTP();
    }
    public void getMessagesByConversationId(String conversationId, Callback callback) {
        //check ipconfig ipaddress for this machine
        Request request = new Request.Builder()
                .url("http://192.168.1.132:8080/conversations/"+conversationId+"/messages")
                .build();
        remote.AsyncRequest(callback, request);
    }
    public void getMessagesIdsByConversation(String conversationId, MessageService.GetMessageIdsCallback callback) {
        Request request = new Request.Builder()
                .url("http://192.168.1.132:8080/conversations/"+conversationId+"/messages?ids_only=true")
                .build();
        remote.AsyncRequest(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() != 200)
                   callback.onFailure(new Exception("Not Success code"));
                String responseBody = response.body().string();
                List<String> ids = JsonToObjectMapper.JsonToString(responseBody);
                callback.onSuccess(ids);
            }
        }, request);
    }

    public void deleteMessage(String messageId, Callback callback) {
        Request request = new Request.Builder()
                .delete()
                .url("http://192.168.1.132:8080/messages/"+messageId)
                .build();
        remote.AsyncRequest(callback, request);
    }
    public void getMessages(List<String> ids, MessageService.GetMessagesCallback callback) {
        String json = null;
        try {
            json = JsonToObjectMapper.StringToJson(ids);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            // Use URLEncoder to encode the entire parameter string
            String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString());
            List<String> formattedIds = ids.stream().map(id -> id.replaceAll("\"", "")).collect(Collectors.toList());

// Construct the URL without URL encoding
            String url = "http://192.168.1.132:8080/messages?ids=" + String.join(",", formattedIds);
            // Include the encoded parameter in the URL
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            remote.AsyncRequest(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful())
                        callback.onFailure(new Exception("Unsuccessfull status code"));
                    else {
                        if (response.code() == 200) {
                            String responseBody = response.body().string();
                            List<MessageDTO> messages = JsonToObjectMapper.JsonToMessages(responseBody);
                            callback.onSuccess(messages.stream().map(Message::new).collect(Collectors.toList()));
                        }
                        else if (response.code() == 204) {
                            List<Message> emptyM = new ArrayList<>();
                            callback.onSuccess(emptyM);
                        }
                    }
                }
            }, request);
        } catch (UnsupportedEncodingException e) {
            // Handle the exception
            callback.onFailure(e);
        }
    }
}
