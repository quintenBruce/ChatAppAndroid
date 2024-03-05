package com.example.blankapplication.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blankapplication.data.models.Conversation;
import com.example.blankapplication.data.repositories.ConversationRepository;
import com.example.blankapplication.helpers.JsonToObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivityViewModel extends ViewModel {
    private ConversationRepository conversationRepository;
    private MutableLiveData<List<Conversation>> conversationsLiveData = new MutableLiveData<>();

    public MainActivityViewModel() {
        conversationRepository = new ConversationRepository();
    }

    public LiveData<List<Conversation>> getConversations() {
        return conversationsLiveData;
    }

    public void getConversationsById(String userId) {
        conversationRepository.getConversationsById(userId, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String s = response.body().string();
                List<Conversation> conversations = JsonToObjectMapper.JsonToConversation(s);
                conversationsLiveData.postValue(conversations);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OkHTTP!", e.getMessage());
            }
        });
    }
}
