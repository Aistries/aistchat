package com.aistriesking.aistchat.Notify;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.aistriesking.aistchat.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationItem> notificationItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNotificationItems(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NotificationAdapter(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem currentItem = notificationItems.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNotification;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNotification = itemView.findViewById(R.id.notificationText);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(NotificationItem item) {
            textViewNotification.setText(item.getNotificationText());
        }
    }
}
