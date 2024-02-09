package com.aistriesking.aistchat;

import java.util.List;

public class ImageItem {
    private String imageUrl;
    private int likeCount;
    private List<String> comments; // Add this line
    private boolean liked;

    public ImageItem(String imageUrl, int likeCount, List<String> comments, boolean liked) {
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.comments = comments;
        this.liked = liked;
    }




    // Add getters and setters for comments
    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isLiked() {
        return liked;
    }

    public int getLikeCount() {
        return likeCount;
    }



    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }


}
