package com.example.melochat.models;

import android.widget.ImageView;

import java.io.Serializable;

public class PostItem implements Serializable {
    private String userId;
    private String userName;
    private String genre;
    private String content;
    private String mediaURL; //url to media
    private String timestamp;

    public PostItem(){}

    public PostItem(String userId, String userName, String genre, String content, String mediaURL, String timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.genre = genre;
        this.content = content;
        this.mediaURL = mediaURL;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
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

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
