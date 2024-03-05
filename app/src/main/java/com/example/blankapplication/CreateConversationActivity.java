package com.example.blankapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.blankapplication.viewmodels.CreateConversationActivityViewModel;
import com.example.blankapplication.viewmodels.MainActivityViewModel;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.blankapplication.databinding.ActivityCreateConversation2Binding;
import com.google.android.material.textfield.TextInputEditText;

public class CreateConversationActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCreateConversation2Binding binding;
    private CreateConversationActivityViewModel viewModel;
    private EditText participantEntry;
    private TextView participantsList;
    private EditText nameEntry;
    private Button createConversationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateConversation2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewModel = new ViewModelProvider(this).get(CreateConversationActivityViewModel.class);;

        participantEntry = findViewById(R.id.participantEntry);
        participantsList = findViewById(R.id.participantsList);
        createConversationButton = findViewById(R.id.createConversationButton);
        nameEntry = findViewById(R.id.nameEntry);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Conversation");

        viewModel.getCreationStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    Intent intent = new Intent(CreateConversationActivity.this, MainActivity.class);
                    CreateConversationActivity.this.startActivity(intent);
                }
                else {
                    Toast.makeText(CreateConversationActivity.this, "Unable to create conversation", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(intent2);
            }
        });

        Button addParcicipantButton = findViewById(R.id.addParticipantButton);
        addParcicipantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = getParticipantEntry();
                if (userName.trim().equals("")) {
                    Toast.makeText(CreateConversationActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                addParticipant(userName.trim());
            }
        });


        createConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getParticipantList().equals("")) {
                    Toast.makeText(CreateConversationActivity.this, "No participants added.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getNameEntry().trim().equals("")) {
                    Toast.makeText(CreateConversationActivity.this, "Please create a conversation name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                createConversation();
            }
        });
    }


//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_create_conversation);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
    private String  getParticipantEntry() {
        String participantUsername = participantEntry.getText().toString();
        return participantUsername;

    }
    private String getParticipantList() {
        return (String) participantsList.getText();
    }
    private String getNameEntry() {
        return nameEntry.getText().toString();
    }
    private void createConversation() {
        Drawable syncIcon = getResources().getDrawable(android.R.drawable.stat_notify_sync);
        createConversationButton.setCompoundDrawablesWithIntrinsicBounds(syncIcon, null, null, null);

        viewModel.setConversationName(getNameEntry());
         viewModel.createConversation();
    }
    private void addParticipant(String userName){
        viewModel.addParticipant(userName);
        String text = participantsList.getText().toString();
        text = text + "- " + userName + "\n";
        participantsList.setText(text);
        participantEntry.setText("");
    }
}