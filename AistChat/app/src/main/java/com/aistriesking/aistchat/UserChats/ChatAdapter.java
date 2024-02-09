package com.aistriesking.aistchat.UserChats;

// ChatAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aistriesking.aistchat.R;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ChatMessage message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat_message, parent, false);
        }

        TextView tvSender = convertView.findViewById(R.id.tvSender);
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);

        if (message != null) {
            tvSender.setText(message.getSender());
            tvMessage.setText(message.getMessage());
        }

        return convertView;
    }
}

