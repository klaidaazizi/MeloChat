package com.example.melochat;

import static java.security.AccessController.getContext;

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

//import com.bumptech.glide.Glide;
import com.example.melochat.models.PostItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FeedActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference postsDatabase;
    private RecyclerView recyclerView;
    private PostRVAdapter rviewAdapter;
    private LinearLayoutManager rLayoutManger;
    private ArrayList<PostItem> postsList;
    //private Map<String, PostItem> posts;
    private DatabaseReference database;


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
                        intent.putExtra("posts",rviewAdapter.getPostsList());
                        startActivity(intent);
                        break;
                    case R.id.action_feed:
                        createRecyclerView(postsList);
                        break;
                }
                return true;
            }
        });

        init(savedInstanceState);
    }


    public void onClick(View view){
        switch (view.getId()){
             case R.id.button_trending:
                showTrendingPosts();
                 break;
            case R.id.button_filter:
                filterPosts();
                break;
        }
    }

    private void showTrendingPosts() {
        ArrayList<PostItem> trendingPosts = new ArrayList<>();
        for (PostItem post: postsList){
            if ((post.getLikes() +post.getCommentsNumber()) >= 10){
                trendingPosts.add(post);
            }
        }
        createRecyclerView(trendingPosts);
    }


    public void filterPosts() {
        ArrayList<PostItem> filteredPosts = new ArrayList<>();
        String[] options = getResources().getStringArray(R.array.genres_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
        builder.setTitle("Filter by genre");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("", options[which]);
                if ("All".equalsIgnoreCase(options[which])){
                    filteredPosts.addAll(postsList);}
                else {
                    for (PostItem post : postsList){
                        if (post.getGenre().equalsIgnoreCase(options[which])) {
                                filteredPosts.add(post);
                            }
                    }
                }
                createRecyclerView(filteredPosts);
                }
            });
        builder.show();
    }

    private void init(Bundle savedInstanceState) {
        createRecyclerView(postsList);
    }

    private void createRecyclerView(ArrayList arrayList) {
        rLayoutManger = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rLayoutManger.setStackFromEnd(true);
        rLayoutManger.setReverseLayout(true);

        recyclerView = findViewById(R.id.recyclerView_feed);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new PostRVAdapter(arrayList);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);
    }

}