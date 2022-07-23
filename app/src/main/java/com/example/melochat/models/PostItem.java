package com.example.melochat.models;

import android.widget.ImageView;

public class PostItem {
    private String userId;
    private String genre;
    private String content;
    private String media; //url to media
    private String timestamp;

    public PostItem(String userId, String genre, String content, String media, String timestamp) {
        this.userId = userId;
        this.genre = genre;
        this.content = content;
        this.media = media;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
