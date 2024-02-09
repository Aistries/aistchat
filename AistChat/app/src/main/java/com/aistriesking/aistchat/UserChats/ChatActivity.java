package com.aistriesking.aistchat.UserChats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aistriesking.aistchat.R;

// ChatActivity.java
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private EditText editTextMessage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessageList;

    private ListView listView;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login or handle authentication as needed
            finish();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("friends/messages");
        editTextMessage = findViewById(R.id.editTextMessage);

        ListView listViewChat = findViewById(R.id.listViewChat);
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessageList);
        listViewChat.setAdapter(chatAdapter);

        setupChatListener();

        button = findViewById(R.id.btnsend);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage();
            }
        });
    }

    private void setupChatListener() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                chatAdapter.add(chatMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle changed data if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removed data if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                // Handle moved data if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    public void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();

        if (!TextUtils.isEmpty(messageText)) {
            ChatMessage chatMessage = new ChatMessage(currentUser.getDisplayName(), messageText);
            databaseReference.push().setValue(chatMessage);

            editTextMessage.setText("");
        }
    }
}
