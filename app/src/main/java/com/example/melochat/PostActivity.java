package com.example.melochat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private EditText postText;
    private String content;
    private Button mediaButton;
    private EditText linkURL;
    private String media;
    private Spinner genreSpinner;
    private String genre;
    private String userId;
    private String userName;
    private String timestamp;
    private DatabaseReference database;
    private DatabaseReference usersDatabase;
    private DatabaseReference postsDatabase;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    //private Map<String, User> users;
    private FirebaseAuth mAuth;
    public ArrayList<PostItem> postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        postText = (EditText) findViewById(R.id.editText_post);
        mediaButton = (Button) findViewById(R.id.button_media);

        genreSpinner = (Spinner) findViewById(R.id.spinner_genre);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genres_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference();
        postsDatabase = database.child("postsWithComments");

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


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_post:
                        Utils.postToastMessage("You're in the Post page!",PostActivity.this);
                        break;
                    case R.id.action_profile:
                        Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                        intent.putExtra("posts",postsList);
                        startActivity(intent);
                        break;
                    case R.id.action_feed:
                        intent = new Intent(PostActivity.this, FeedActivity.class);
                        intent.putExtra("posts", postsList);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            userId = currentUser.getUid();
            userName = currentUser.getDisplayName();
        }
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
            Integer likes = Math.toIntExact((long) postSnapshot.child("likes").getValue());
            Iterable<DataSnapshot> commentsSnapshot = (Iterable<DataSnapshot>) postSnapshot.child("comments").getChildren();
            ArrayList<String> comments = new ArrayList<>();
                for (DataSnapshot snapshot : commentsSnapshot) {
                    String comment = (String) snapshot.getValue();
                    comments.add(comment);
                }
            Integer reposts = Math.toIntExact((long) postSnapshot.child("reposts").getValue());
            postsList.add(new PostItem(userId,userName,genre,content,media,timestamp,likes,comments,reposts));
        }
        //Log.d("posts",postsList.toString());
    }

    public void onMediaButtonClick(View view){
        final View alert = getLayoutInflater().inflate(R.layout.dialog_link_collector,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Create dialog box and add new link item if URL is valid
        AlertDialog dialog = builder.setTitle("Media Link").setView(alert).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                linkURL = alert.findViewById(R.id.linkURL);
                if (Utils.isValidURL(linkURL.getText().toString())) {
                    media = linkURL.getText().toString();
                } else{
                    Utils.postToastMessage("Invalid URL! Please try again.", PostActivity.this);
                }
            }
        }).create();
        dialog.show();
    }

    public void addPost(View view) {
        content = postText.getText().toString();
        genre = genreSpinner.getSelectedItem().toString();
        timestamp = dateFormat.format(new Date()); //this is gonna be the post id
        PostItem post = new PostItem(userId,userName,genre,content,media,timestamp);
        database.child("postsWithComments").child(timestamp).setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utils.postToastMessage("Successfully created new post!",PostActivity.this);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.postToastMessage("Failed to create new post.",PostActivity.this);
                    }
                });
    }



}
