package com.example.melochat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private Button signoutButton;
    private FirebaseAuth mAuth;
    private ImageView profilePhoto;
    private TextView emailText;
    private TextView nameText;
    private StorageReference mStorage;
    private StorageReference profileImagesRef;

    private RecyclerView recyclerView;
    private PostRVAdapter rviewAdapter;
    private LinearLayoutManager rLayoutManger;
    private ArrayList<PostItem> postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        // Initialize widgets
        profilePhoto = (ImageView) findViewById(R.id.userProfileImage);
        emailText = (TextView) findViewById(R.id.textView_email);
        nameText = (TextView) findViewById(R.id.textView_name);
        signoutButton = (Button) findViewById(R.id.button_profile_signout);

        postsList = (ArrayList<PostItem>) getIntent().getSerializableExtra("posts");

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_post:
                        Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        Utils.postToastMessage("You're in the Profile page!",ProfileActivity.this);
                        break;
                    case R.id.action_feed:
                        intent = new Intent(ProfileActivity.this, FeedActivity.class);
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
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        profileImagesRef = mStorage.child("profileImages");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            String currentUID = currentUser.getUid();
            nameText.setText(currentUser.getDisplayName());
            emailText.setText(currentUser.getEmail());

            // Get URL of profile image named by their userID and update UI
            profileImagesRef.child(currentUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePhoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ProfileActivity.this, "Failed to Access Profile Photo.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Filter postsList by current user
            ArrayList<PostItem> filteredPosts = new ArrayList<>();
            for (PostItem post : postsList){
                if (post.getUserId().equals(currentUID)){
                    filteredPosts.add(post);
                }
            }
            createRecyclerView(filteredPosts);
        }
    }

    private void init(Bundle savedInstanceState) {
        createRecyclerView(postsList);
    }

    private void createRecyclerView(ArrayList arrayList) {
        rLayoutManger = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rLayoutManger.setStackFromEnd(true);
        rLayoutManger.setReverseLayout(true);

        recyclerView = findViewById(R.id.recyclerView_profile);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new PostRVAdapter(arrayList);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);
    }
}