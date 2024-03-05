package com.example.blankapplication.viewmodels;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blankapplication.data.UserInformation;
import com.example.blankapplication.data.dtos.ConversationDTO;
import com.example.blankapplication.data.repositories.ConversationRepository;
import com.example.blankapplication.data.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

public class CreateConversationActivityViewModel extends ViewModel {
    private ConversationRepository conversationRepository;
    private UsersRepository usersRepository;
    public CreateConversationActivityViewModel() {
        usersRepository = new UsersRepository();
        this.conversationRepository = new ConversationRepository();
    }

    private MutableLiveData<String> conversationName = new MutableLiveData<>();
    private MutableLiveData<List<String>> participants = new MutableLiveData<>();
    private MutableLiveData<String> participantEntry = new MutableLiveData<>();
    private MutableLiveData<String> creationStatus = new MutableLiveData<>();

    public MutableLiveData<String> getCreationStatus() {
        return creationStatus;
    }

    public void setConversationName(String value) {
        conversationName.setValue(value);
    }

    public LiveData<String> getConversationName() {
        return conversationName;
    }

    public void setParticipants(List<String> _participants) {
        participants.setValue(_participants);
    }

    public LiveData<String> getParticipants() {
        return conversationName;
    }
    private void setCreationStatus(String s) {
        creationStatus.setValue(s);
    }
    public void addParticipant(String participant) {
       List<String> participantsValue = participants.getValue();
       if (participantsValue == null)
           participantsValue = new ArrayList<>();
       participantsValue.add(participant);
       setParticipants(participantsValue);
    }
    public void createConversation() {
        ConversationDTO conversation = new ConversationDTO();
        conversation.setName(conversationName.getValue());
        //
        //Instead of Conversation object, create ConversationDTO object
        //
        conversation.setParticipants(participants.getValue());
        String id_ = UserInformation.getInstance().getUserId();
        conversation.addParticipant(UserInformation.getInstance().getUserId());
        conversationRepository.createConversation(conversation, new ConversationRepository.ConversationRepositoryCallback() {
            @Override
            public void OnSuccess() {
                // Ensure UI updates run on the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        setCreationStatus("Success");
                    }
                });
            }

            @Override
            public void OnFailure() {
                // Ensure UI updates run on the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        setCreationStatus("Failure");
                    }
                });
            }
        });
    }
}
