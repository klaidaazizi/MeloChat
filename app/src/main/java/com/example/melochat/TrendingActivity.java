package com.example.melochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TrendingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        setTitle("Trending");
        
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_post:
                        Intent intent = new Intent(TrendingActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        intent = new Intent(TrendingActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_feed:
                        intent = new Intent(TrendingActivity.this, FeedActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //TODO Show posts that have the most likes
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

    private void filterPosts() {
    }
}