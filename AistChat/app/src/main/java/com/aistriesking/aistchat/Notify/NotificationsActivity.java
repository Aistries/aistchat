package com.aistriesking.aistchat.Notify;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.aistriesking.aistchat.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    private static final String NOTIFICATION_PREF = "NotificationPref";
    private static final String RECENT_POST_KEY = "recent_post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch recent post from SharedPreferences
        List<NotificationItem> notificationItems = getRecentPost();

        adapter = new NotificationAdapter(notificationItems);
        recyclerView.setAdapter(adapter);
    }

    private List<NotificationItem> getRecentPost() {
        List<NotificationItem> recentPosts = new ArrayList<>();

        // Retrieve recent post from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(NOTIFICATION_PREF, MODE_PRIVATE);
        String recentPostText = sharedPreferences.getString(RECENT_POST_KEY, null);

        if (recentPostText != null) {
            // Use the constructor without the Uri for cases where there is no image URI
            recentPosts.add(new NotificationItem(recentPostText));
        }

        return recentPosts;
    }

}