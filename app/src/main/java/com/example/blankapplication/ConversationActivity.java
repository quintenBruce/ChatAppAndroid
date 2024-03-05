package com.example.blankapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.blankapplication.Adapters.MessageRecyclerViewAdapter;
import com.example.blankapplication.data.dtos.MessageDTO;
import com.example.blankapplication.data.models.MessageRecycler;
import com.example.blankapplication.viewmodels.ConversationActivityViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.blankapplication.databinding.ActivityConversationBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConversationActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.ItemClickListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityConversationBinding binding;
    private MessageRecyclerViewAdapter adapter;
    private ConversationActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent = getIntent();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ConversationActivityViewModel.class);

        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("conversationName"));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(intent2);
            }
        });

        List<MessageRecycler> messageList = new ArrayList<>();


        RecyclerView recyclerView = findViewById(R.id.messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageRecyclerViewAdapter(this, messageList);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swiperefreshMessages);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    viewModel.getMessagesByConversationId();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                refreshLayout.setRefreshing(false);
                return;
            }
        });




        viewModel.getMessages().observe(this, messages -> {
            adapter.updateItems(messages);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        });

        try {
            String conversationId = intent.getStringExtra("conversationId");
            viewModel.setConversationId(conversationId);
            //viewModel.getUsers(participantIds);
            viewModel.getMessagesByConversationId();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Button sendButton = (Button) findViewById(R.id.sendButton);
        EditText editText = (EditText) findViewById(R.id.messageInput);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(ConversationActivity.this, "Message is empty...", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendButton.setEnabled(false);
                refreshLayout.setRefreshing(true);
                viewModel.sendMessage(editText.getText().toString(), new ConversationActivityViewModel.SendMessageCallback() {
                    @Override
                    public void OnFailure() {
                        sendButton.setEnabled(true);
                        refreshLayout.setRefreshing(false);
                        runOnUiThread(() -> {
                            Toast.makeText(ConversationActivity.this, "Failed to send message.", Toast.LENGTH_SHORT);
                        });
                    }

                    @Override
                    public void OnSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendButton.setEnabled(true);
                                refreshLayout.setRefreshing(false);
                                editText.setText("");
                                Toast.makeText(ConversationActivity.this, "Message successfully sent.", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
            }
        });







    }
    @Override
    public void onItemClick(View view, int position) {
        TextView dateTime = view.findViewById(R.id.dateTime);
        ViewGroup.LayoutParams layoutParams = dateTime.getLayoutParams();

        // Update the height to wrap content
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // Apply the updated layout parameters to the view
        dateTime.setLayoutParams(layoutParams);
    }
}