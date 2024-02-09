package com.aistriesking.aistchat;

import android.app.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the incoming message and show a notification
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String title, String body) {
        // Code to display a notification
        // You can customize this based on your needs
        // ...

        CharSequence text = "you have one new unread message.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this /* MyActivity */, text, duration);
        toast.show();
    }
}

