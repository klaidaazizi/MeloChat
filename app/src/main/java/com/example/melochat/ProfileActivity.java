package com.example.melochat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profilePhoto;
    private TextView emailText;
    private TextView nameText;
    //private TextView ageRangeText;
    private StorageReference mStorage;
    private StorageReference profileImagesRef;

    private ArrayList<PostItem> postsList;
    private RecyclerView recyclerView;
    private PostRVAdapter rviewAdapter;
    private LinearLayoutManager rLayoutManager;
    private DatabaseReference database;
    private DatabaseReference postsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Profile");
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        // check if user is signed in (non-null) and updates UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        profileImagesRef = mStorage.child("profileImages");
        // Initialize widgets
        profilePhoto = (ImageView) findViewById(R.id.userProfileImage);
        emailText = (TextView) findViewById(R.id.textView_email);
        nameText = (TextView) findViewById(R.id.textView_name);

        postsList = (ArrayList<PostItem>) getIntent().getSerializableExtra("posts");
        setContentView(R.layout.activity_profile);

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
                        Utils.postToastMessage("You're in the Profile page!", ProfileActivity.this);
//                        intent = new Intent(ProfileActivity.this, FeedActivity.class);
//                        intent.putExtra("profile", rviewAdapter.getPostsList());
//                        startActivity(intent);
//                        onStart();
                        assert currentUser != null;
                        nameText.setText(currentUser.getDisplayName());
                        emailText.setText(currentUser.getEmail());
                        showUserPosts();
                        break;
                    case R.id.action_feed:
                        /*intent = new Intent(ProfileActivity.this, FeedActivity.class);
                        intent.putExtra("posts", postsList);
                        startActivity(intent);*/
                        createRecyclerView(postsList);
                        break;
                }
                return true;
            }
        });
        init(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference();
        postsDatabase = database.child("posts");
        postsList = new ArrayList<>();
        postsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatePosts(snapshot.getChildren());
                //Log.e("POSTS: ", postsList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utils.postToastMessage("No Posts by user", ProfileActivity.this);
            }
        });
    }

    private void filterPostsByUser(){
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        profileImagesRef = mStorage.child("profileImages");
        if (currentUser.isEmailVerified()){
            nameText.setText(currentUser.getDisplayName());
            emailText.setText(currentUser.getEmail());
            Toast.makeText(ProfileActivity.this, "Login Success.",
                    Toast.LENGTH_SHORT).show();
            profileImagesRef.child(currentUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePhoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Profile photo failed to load.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*@Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        profileImagesRef = mStorage.child("profileImages");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            nameText.setText(currentUser.getDisplayName());
            emailText.setText(currentUser.getEmail());
            Toast.makeText(ProfileActivity.this, "Login Success.",
                    Toast.LENGTH_SHORT).show();

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
        }
    }*/


    private void showUserPosts(){
        ArrayList<PostItem> userPosts = new ArrayList<>();
        //String[] options = getResources().getStringArray(R.array.user_array)
        for (PostItem post : postsList){
            if (currentUser.isEmailVerified()){
                filterPostsByUser();
            } else if (post != null){
                userPosts.add(post);
            }
        }
        createRecyclerView(userPosts);
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

    private void init(Bundle savedInstanceState){
        createRecyclerView(postsList);
    }

    private void createRecyclerView(ArrayList<PostItem> profileList) {
        rLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rLayoutManager.setStackFromEnd(true);
        rLayoutManager.setReverseLayout(true);

        recyclerView = findViewById(R.id.recyclerView_profile);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new PostRVAdapter(profileList);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManager);
    }

}