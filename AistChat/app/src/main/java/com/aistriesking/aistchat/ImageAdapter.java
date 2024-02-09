package com.aistriesking.aistchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<ImageItem> imageItems;
    private LikeClickListener likeClickListener;
    private UnlikeClickListener unlikeClickListener;
    private CommentClickListener commentClickListener;


    public ImageAdapter(List<ImageItem> imageItems, LikeClickListener likeClickListener,
                        UnlikeClickListener unlikeClickListener, CommentClickListener commentClickListener) {
        this.imageItems = imageItems;
        this.likeClickListener = likeClickListener;
        this.unlikeClickListener = unlikeClickListener;
        this.commentClickListener = commentClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem currentItem = imageItems.get(position);

        Picasso.get().load(currentItem.getImageUrl()).fit().centerCrop().into(holder.imageView);

        // Set like, unlike, and comment click listeners
        holder.likeButton.setOnClickListener(v -> likeClickListener.onLikeClick(position, holder.likeButton, holder.unlikeButton, holder.likeCountTextView));
        holder.unlikeButton.setOnClickListener(v -> unlikeClickListener.onUnlikeClick(position, holder.likeButton, holder.unlikeButton, holder.likeCountTextView));
        holder.commentButton.setOnClickListener(v -> commentClickListener.onCommentClick(position));

        // Update the like count
        holder.likeCountTextView.setText(String.valueOf(currentItem.getLikeCount()));
        // Set like button visibility based on the liked status
        holder.likeButton.setVisibility(currentItem.isLiked() ? View.GONE : View.VISIBLE);
        holder.unlikeButton.setVisibility(currentItem.isLiked() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageButton likeButton;
        ImageButton unlikeButton;
        ImageButton commentButton;
        TextView likeCountTextView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            likeButton = itemView.findViewById(R.id.likeButton);
            unlikeButton = itemView.findViewById(R.id.unlikeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
        }
    }

    public interface LikeClickListener {
        void onLikeClick(int position, ImageButton likeButton, ImageButton unlikeButton, TextView likeCountTextView);
    }

    public interface UnlikeClickListener {
        void onUnlikeClick(int position, ImageButton likeButton, ImageButton unlikeButton, TextView likeCountTextView);
    }


    public interface CommentClickListener {
        void onCommentClick(int position);
    }



}
