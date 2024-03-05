package com.example.blankapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.blankapplication.Adapters.ConversationRecyclerViewAdapter;
import com.example.blankapplication.data.AppDatabase;
import com.example.blankapplication.data.UserInformation;
import com.example.blankapplication.data.models.Conversation;

import com.example.blankapplication.data.repositories.MessageLocalRepository;
import com.example.blankapplication.viewmodels.MainActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.bson.types.ObjectId;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity implements ConversationRecyclerViewAdapter.ItemClickListener {
    ConversationRecyclerViewAdapter adapter;

    private SwipeRefreshLayout ref;

    private MainActivityViewModel viewModel;
    private MessageLocalRepository messageLocalRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        showSelectionDialog();


        setContentView(R.layout.activity_main);

        ref = findViewById(R.id.swiperefresh);


        List<Conversation> defConvs = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.conversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConversationRecyclerViewAdapter(this, defConvs);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        viewModel.getConversations().observe(this, conversations -> {
            adapter.updateItems(conversations);
            ref.setRefreshing(false);
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.conversations_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.createConversation) {
                            createConversation();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showSelectionDialog();
            }
        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Conversations");
        setSupportActionBar(myToolbar);
    }

    private void createConversation() {
        Intent intent = new Intent(this, CreateConversationActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(view.getContext(), ConversationActivity.class);
        Conversation conversation = (Conversation) view.getTag();
        String conversationId = conversation.getId().toString();
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("conversationName", conversation.getName());
        view.getContext().startActivity(intent);
    }

    private void showSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a user");

        // Define the dropdown items
        String[] options = {"658353586f91730d26ecafcc",
                "65835ab5b5e32bce54a65ef5",
                "658e1d80dbcbc22cca25d49f",
                "657e5c7e16ab5a5bbf7be0cb"};

        builder.setItems(options, (dialog, which) -> {
            // Handle the selection here
            Toast.makeText(MainActivity.this, "Selected: " + options[which], Toast.LENGTH_SHORT).show();

            UserInformation.getInstance().setUserId(options[which]);

            // Continue with the main activity setup after selection
            viewModel.getConversationsById(options[which]);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}