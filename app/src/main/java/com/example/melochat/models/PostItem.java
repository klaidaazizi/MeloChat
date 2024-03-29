package com.example.melochat.models;

import android.widget.ImageView;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;

public class PostItem implements Serializable {
    private String userId;
    private String userName;
    private String genre;
    private String content;
    private String media;
    private String timestamp;
    private Integer likes;
    private ArrayList<String> comments;
    private Integer reposts;

    public PostItem(){}

    public PostItem(String userId, String userName, String genre, String content, String media, String timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.genre = genre;
        this.content = content;
        this.media = media;
        this.timestamp = timestamp;
        this.likes = 0;
        this.comments = new ArrayList<String>();
        this.reposts = 0;
    }

    public PostItem(String userId, String userName, String genre, String content, String media, String timestamp,
                    Integer likes, ArrayList<String> comments, Integer reposts) {
        this.userId = userId;
        this.userName = userName;
        this.genre = genre;
        this.content = content;
        this.media = media;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
        this.reposts = reposts;
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

    public Integer getLikes() { return likes;}
    public void addLike() {this.likes++;}

    public ArrayList<String> getComments() { return comments; }
    public Integer getCommentsNumber() { return this.comments.size(); }
    public void addComment(String comment) { this.comments.add(comment); }

    public Integer getReposts() { return reposts; }
    public void addRepost() { this.reposts++; }


}
