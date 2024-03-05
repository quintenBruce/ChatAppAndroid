package com.example.blankapplication.data.repositories;

import androidx.annotation.NonNull;

import com.example.blankapplication.data.models.User;
import com.example.blankapplication.data.remote.OkHTTP;
import com.example.blankapplication.helpers.JsonToObjectMapper;
import com.example.blankapplication.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UsersRepository {
    private OkHTTP remote;
    public UsersRepository() {
        remote = new OkHTTP();
    }
    public void getUsers(List<String> participantIds, Callback callback) {
        //check ipconfig ipaddress for this machine
        Request request = new Request.Builder()
                .url("http://192.168.1.132:8080/users?ids="+participantIds.toString())
                .build();
        remote.AsyncRequest(callback, request);
    }

    public void getUser(String userId, UserService.GetUserCallback callback)  {
        Request request = new Request.Builder()
                .url("http://192.168.1.132:8080/users/"+userId)
                .build();
        remote.AsyncRequest(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() != 200)
                    callback.onFailure(new Exception("Not 200 response"));

                User user = null;
                try {
                    String responseBody = null;
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }
                    user = JsonToObjectMapper.JsonToUser(responseBody);
                } catch (IOException e) {
                    callback.onFailure(e);
                }
                callback.onSuccess(user);
            }
        }, request);
    }
}
