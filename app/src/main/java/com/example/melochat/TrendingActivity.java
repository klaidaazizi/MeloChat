package com.example.melochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.melochat.models.PostItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrendingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    public ArrayList<PostItem> postsList;
    private DatabaseReference postsDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);
        mAuth = FirebaseAuth.getInstance();
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
                        intent.putExtra("posts",postsList);
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
        database = FirebaseDatabase.getInstance().getReference();
        postsDatabase = database.child("posts");
        postsList = new ArrayList<>();
        postsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatePosts(snapshot.getChildren());
                Log.e("POSTS: ", postsList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //TODO Show posts that have the most likes
    }
    private void updatePosts(Iterable<DataSnapshot> children) {
        for (DataSnapshot postSnapshot : children) {
            //String post = postSnapshot.getKey();
            String timestamp = (String) postSnapshot.child("timestamp").getValue();
            String genre = (String) postSnapshot.child("genre").getValue();
            String userId = (String) postSnapshot.child("userId").getValue();
            String userName = (String) postSnapshot.child("userName").getValue();
            String content = (String) postSnapshot.child("content").getValue();
            String media = (String) postSnapshot.child("media").getValue();
            postsList.add(new PostItem(userId,userName,genre,content,media,timestamp));
        }
        //Log.d("posts",postsList.toString());
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_foryou:
                Intent feedIntent = new Intent(this, FeedActivity.class);
                feedIntent.putExtra("posts", postsList);
                startActivity(feedIntent);
                break;
            case R.id.button_filter:
                filterPosts();
                break;
        }
    }

    public void filterPosts() {
        String[] options = getResources().getStringArray(R.array.genres_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(TrendingActivity.this);
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