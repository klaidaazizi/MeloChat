package com.example.melochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle("For you");

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_post:
                        Intent intent = new Intent(FeedActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        intent = new Intent(FeedActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_feed:
                        intent = new Intent(FeedActivity.this, FeedActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

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