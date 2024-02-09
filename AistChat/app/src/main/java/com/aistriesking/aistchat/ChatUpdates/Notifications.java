package com.aistriesking.aistchat.ChatUpdates;

// NotificationsActivity.java
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aistriesking.aistchat.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class Notifications extends AppCompatActivity {

    private TextView notificationTitleTextView;
    private TextView notificationBodyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

//        notificationTitleTextView = findViewById(R.id.notificationTitleTextView);
//        notificationBodyTextView = findViewById(R.id.notificationBodyTextView);

        // Subscribe to the FCM topic related to group chat
        subscribeToGroupChatTopic();
    }

    private void subscribeToGroupChatTopic() {
        // Replace "groupChat" with the actual topic name used in your Firebase Cloud Functions
        FirebaseMessaging.getInstance().subscribeToTopic("Aistries")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully subscribed to the topic
                        // You can customize this based on your needs
                        displayNotification("Welcome to the Group Chat", "You will receive notifications for new messages.");
                    } else {
                        // Failed to subscribe to the topic
                        displayNotification("Error", "Failed to subscribe to notifications.");
                    }
                });
    }

    private void displayNotification(String title, String body) {
        // Display the notification in the activity
        notificationTitleTextView.setText(title);
        notificationBodyTextView.setText(body);
    }
}
