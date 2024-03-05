package com.example.blankapplication.data.repositories;

import androidx.annotation.NonNull;

import com.example.blankapplication.data.dtos.ConversationDTO;
import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.data.remote.OkHTTP;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import

public class ConversationRepository {

    public interface ConversationRepositoryCallback {
        public void OnSuccess();

        public void OnFailure();
    }

    private OkHTTP remote;

    public ConversationRepository() {
        remote = new OkHTTP();
    }

    public void getConversations(Callback callback) {
        String baseUrl = BuildConfig.DEV_BASE_URL;
        Request request = new Request.Builder()
                .url(BuildConfig.DEV_BASE_URL + "conversations")
                .build();
        BuildC
        remote.AsyncRequest(callback, request);
    }

    public void getConversationsById(String id, Callback callback) {
        Request request = new Request.Builder()
                .url("http://192.168.1.132:8080/conversations?participantId=" + id)
                .build();
        remote.AsyncRequest(callback, request);
    }

    public void putMessage(MessageDTO message, Callback callback) {
        String Json = message.toJson();


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Json);

        Request request = new Request.Builder().put(body)
                .url("http://192.168.1.132:8080/conversations/" + message.getConversationId() + "/messages")
                .build();
        remote.AsyncRequest(callback, request);
    }

    public void createConversation(ConversationDTO conversationDTO, ConversationRepositoryCallback callback) {
        String Json = conversationDTO.toJson();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Json);

        Request request = new Request.Builder().post(body)
                .url("http://192.168.1.132:8080/conversations")
                .build();
        remote.AsyncRequest(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.OnFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() != 200) {
                    callback.OnFailure();
                    return;
                }
                callback.OnSuccess();
            }
        }, request);
    }
}
