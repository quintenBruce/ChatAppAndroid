package com.example.blankapplication.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.blankapplication.ConversationActivity;
import com.example.blankapplication.data.AppDatabase;
import com.example.blankapplication.data.UserInformation;
import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.data.models.Message;
import com.example.blankapplication.data.models.MessageRecycler;
import com.example.blankapplication.data.repositories.ConversationRepository;
import com.example.blankapplication.data.repositories.MessageLocalRepository;
import com.example.blankapplication.data.repositories.MessageRepository;
import com.example.blankapplication.data.repositories.UsersRepository;
import com.example.blankapplication.helpers.JsonToObjectMapper;
import com.example.blankapplication.helpers.MessageMapper;
import com.example.blankapplication.services.MessageService;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ConversationActivityViewModel extends AndroidViewModel {
    private MessageRepository messageRepository;
    private ConversationRepository conversationRepository;
    private UsersRepository usersRepository;
    private MessageMapper messageMapper;
    private MutableLiveData<List<MessageRecycler>> messagesLiveData = new MutableLiveData<>();

    private String conversationId;
    private MessageService messageService;
    private MessageLocalRepository messageLocalRepository;

    public interface SendMessageCallback {
        public void OnSuccess();

        public void OnFailure();
    }


    public ConversationActivityViewModel(Application application) {
        super(application);
        // Initialization logic here
        messageRepository = new MessageRepository();
        usersRepository = new UsersRepository();
        this.conversationRepository = new ConversationRepository();
        messageMapper = new MessageMapper(application);
//        AppDatabase database = Room.databaseBuilder(application, AppDatabase.class, "message-db").build();
//        messageLocalRepository = new MessageLocalRepository(database.messageDao());
        this.messageService = new MessageService(application);
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public LiveData<List<MessageRecycler>> getMessages() {
        return messagesLiveData;
    }

    public void getMessagesByConversationId() {
        messageService.getMessageIdsByConversation(conversationId, new MessageService.GetMessageIdsCallback() {
            @Override
            public void onSuccess(List<String> ids) {
                Log.d("ids", ids.toString());
                messageService.getMessages(ids, new MessageService.GetMessagesCallback() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        List<MessageRecycler> recyclers = messageMapper.messageToRecycler(messages, UserInformation.getInstance().getUserId());
                        messagesLiveData.postValue(recyclers);
                        Log.d("Messages", messages.toString());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e("Messages", "fatal error");
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d("ids", e.getMessage());
            }
        });
//        messageRepository.getMessagesByConversationId(conversationId, new Callback() {
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                String s = response.body().string();
//                List<MessageDTO> messages = JsonToObjectMapper.JsonToMessages(s);
//                Map<MessageDTO, MessageRecycler> messageRecyclerMap = new ConcurrentHashMap<>();
//
//                for (MessageDTO messageDTO : messages) {
//                    messageMapper.convertToMessageRecycler(messageDTO, new MessageMapper.MessageRecyclerCallback() {
//                        @Override
//                        public void onMessageRecyclerCreated(MessageRecycler messageRecycler) {
//                            messageRecyclerMap.put(messageDTO, messageRecycler);
//                            if (messageRecyclerMap.size() == messages.size()) {
//                                // All conversions completed, reconstruct the list with original order
//                                List<MessageRecycler> messageRecyclerList = new ArrayList<>();
//                                for (MessageDTO msgDTO : messages) {
//                                    messageRecyclerList.add(messageRecyclerMap.get(msgDTO));
//                                }
//
//                                messagesLiveData.postValue(messageRecyclerList);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String errorMessage) {
//                            // Handle failure
//                            // You might need additional logic here based on your error handling needs
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                // Handle failure
//            }
//        });
    }

    public void sendMessage(String content, SendMessageCallback callback) {
        MessageDTO message = new MessageDTO();

        message.setContent(content);
        String userId = UserInformation.getInstance().getUserId();

        message.setConversationId(new ObjectId(conversationId));
        message.setDate(new Date());
        message.setSenderId(new ObjectId(userId));
        conversationRepository.putMessage(message, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() != 200)
                    callback.OnFailure();
                String s = response.body().string();
                MessageDTO message = JsonToObjectMapper.JsonToMessage(s);
                messageMapper.convertToMessageRecycler(message, new MessageMapper.MessageRecyclerCallback() {
                    @Override
                    public void onMessageRecyclerCreated(MessageRecycler messageRecycler) {
                        addMessage(messageRecycler);
                        callback.OnSuccess();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.OnFailure();
                    }
                });
            }


            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OkHTTP", e.getMessage());
                callback.OnFailure();
            }
        });
    }
    private void addMessage(MessageRecycler messageRecycler) {
        List<MessageRecycler> currentMessages = messagesLiveData.getValue();
        if (currentMessages == null)
            currentMessages = new ArrayList<>();
        currentMessages.add(messageRecycler);
        messagesLiveData.postValue(currentMessages);
    }
}
