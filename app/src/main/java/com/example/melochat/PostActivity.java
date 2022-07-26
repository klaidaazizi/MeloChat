package com.example.melochat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
    private String timestamp;
    private DatabaseReference mDatabase;
    private DatabaseReference usersDatabase;
    private DatabaseReference postsDatabase;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    //private Map<String, User> users;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mAuth = FirebaseAuth.getInstance();
        postText = (EditText) findViewById(R.id.editText_post);
        mediaButton = (Button) findViewById(R.id.button_media);

        // Resource: https://developer.android.com/guide/topics/ui/controls/spinner
        genreSpinner = (Spinner) findViewById(R.id.spinner_genre);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genres_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genreSpinner.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get current user
        usersDatabase = mDatabase.child("users");
        usersDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Utils.postToastMessage( "Error getting data from database",PostActivity.this);
                } else{
                    //updateUsers(task.getResult().getChildren());
                    //getUser();
                }
            }
        });
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //updateUsers(snapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        postsDatabase = mDatabase.child("posts");

        //TODO Create new post and add to database


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
                        startActivity(intent);
                        break;
                    case R.id.action_feed:
                        intent = new Intent(PostActivity.this, FeedActivity.class);
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
        }
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
        PostItem post = new PostItem(userId,genre,content,media,timestamp);
        mDatabase.child("posts").child(timestamp).setValue(post)
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

//    private void getUser() {
//        if (users.containsKey(currentUser)) {
//            User currUser = users.get(currentUser);
//            usersDatabase.child(currentUser).setValue(currUser).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(this, "Current user loaded successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }


}
