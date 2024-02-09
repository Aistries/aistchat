package com.aistriesking.aistchat.Notify;

import android.net.Uri;

public class NotificationItem {
    private String notificationText;
    private Uri imageUri;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public NotificationItem(String notificationText) {
        this.notificationText = notificationText;
    }

    public NotificationItem(String notificationText, Uri imageUri) {
        this.notificationText = notificationText;
        this.imageUri = imageUri;
    }

    public String getNotificationText() {
        return notificationText;
    }
}
