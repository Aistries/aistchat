package com.aistriesking.aistchat.UserComments;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment implements Parcelable {
    private String commentText;
    private long timestamp;

    public Comment(String commentText) {
        this.commentText = commentText;
        this.timestamp = System.currentTimeMillis(); // Set current time as the timestamp
    }

    // New constructor with two String parameters
    public Comment(String commentText, String timestamp) {
        this.commentText = commentText;
        // Parse timestamp string to long; replace with your logic if needed
        this.timestamp = parseTimestamp(timestamp);
    }


    private long parseTimestamp(String timestamp) {
        // Implement your logic to parse timestamp string to long
        // For example, you can use SimpleDateFormat to parse the string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            if (date != null) {
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L; // Default value if parsing fails
    }

    public String getFormattedTimestamp() {
        // Format the timestamp for display
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    protected Comment(Parcel in) {
        commentText = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getCommentText() {
        return commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return commentText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentText);
        dest.writeLong(timestamp);
    }
}
