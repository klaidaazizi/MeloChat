package com.example.melochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeedActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference postsDatabase;
    private RecyclerView recyclerView;
    private PostRVAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;
    private ArrayList<PostItem> postsList;
    //private Map<String, PostItem> posts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("For you");

        postsList = (ArrayList<PostItem>) getIntent().getSerializableExtra("posts");
        setContentView(R.layout.activity_feed);


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


        init(savedInstanceState);
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

    private void init(Bundle savedInstanceState) {
        createRecyclerView();
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView_feed);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new PostRVAdapter(postsList);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);
    }

}