package com.example.melochat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_following:
                Intent followingIntent = new Intent(this, FollowingActivity.class);
                startActivity(followingIntent);
                break;
            case R.id.button_trending:
                Intent trendingIntent = new Intent(this, TrendingActivity.class);
                startActivity(trendingIntent);
                break;
            case R.id.button_filter:
                filterPosts();
                break;
        }
    }

    public void filterPosts() {
        String[] options = getResources().getStringArray(R.array.genres_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
        builder.setTitle("Filter by genre");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Select posts by genre
                }
            });
        builder.show();
    }


}