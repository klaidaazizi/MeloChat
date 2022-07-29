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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePhoto;
    private TextView emailText;
    private TextView nameText;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private StorageReference profileImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        // Initialize widgets
        profilePhoto = (ImageView) findViewById(R.id.userProfileImage);
        emailText = (TextView) findViewById(R.id.textView_email);
        nameText = (TextView) findViewById(R.id.textView_name);
        profilePhoto = findViewById(R.id.userProfileImage);

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
    }
}