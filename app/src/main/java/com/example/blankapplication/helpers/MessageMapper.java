package com.example.blankapplication.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.blankapplication.ConversationActivity;
import com.example.blankapplication.data.UserInformation;
import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.data.models.Message;
import com.example.blankapplication.data.models.MessageRecycler;
import com.example.blankapplication.data.models.User;
import com.example.blankapplication.services.UserService;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessageMapper {

    private UserService userService; // Inject UserRepository here via constructor or DI

    public MessageMapper(Context context) {
        userService = new UserService(context);
    }

    public interface MessageRecyclerCallback {
        void onMessageRecyclerCreated(MessageRecycler messageRecycler);

        void onFailure(String errorMessage);
    }
    public List<MessageRecycler> messageToRecycler(List<Message> messageList, String userId) {
        List<MessageRecycler> recyclers = new ArrayList<>();
        for (Message message: messageList) {
            MessageRecycler recycler = new MessageRecycler(message);
            if (message.getSenderId().equals(userId))
                recycler.setSender(true);
            recyclers.add(recycler);
        }
        return recyclers;
    }

    public void convertToMessageRecycler(MessageDTO messageDTO, MessageRecyclerCallback callback) {
        String senderId = messageDTO.getSenderId().toString();

        userService.GetUser(senderId, new UserService.GetUserCallback() {
            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onSuccess(User user) {
                    String senderUsername = user.getUserName();
                    MessageRecycler messageRecycler = createMessageRecycler(messageDTO, senderUsername);
                    callback.onMessageRecyclerCreated(messageRecycler);
            }
        });
    }

    private MessageRecycler createMessageRecycler(MessageDTO messageDTO, String senderUsername) {
        MessageRecycler messageRecycler = new MessageRecycler();
        messageRecycler.setId(messageDTO.getId().toHexString());
        messageRecycler.setContent(messageDTO.getContent());
        messageRecycler.setDate(messageDTO.getDate());
        messageRecycler.setSenderUserName(senderUsername);
        String userId = UserInformation.getInstance().getUserId();
        String currentid = messageDTO.getSenderId().toHexString();
        messageRecycler.setSender(userId.equals(currentid));

        Date date = messageDTO.getDate();

        // Convert java.util.Date to Instant
        Instant instant = date.toInstant();

        // Get the default system time zone (or specify your desired ZoneId)
        ZoneId systemZone = ZoneId.systemDefault();

        // Get the ZoneOffset at a specific instant (using system default time zone)
        ZoneOffset offset = systemZone.getRules().getOffset(instant);

        // Create OffsetDateTime using the Instant and the determined offset
        OffsetDateTime timeWithOffset = instant.atOffset(offset);

        // Format OffsetDateTime into a pretty string including offset and in 12-hour format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
        String formattedTime = timeWithOffset.format(formatter);
        messageRecycler.setFormattedDate(formattedTime);

        return messageRecycler;
    }
}
