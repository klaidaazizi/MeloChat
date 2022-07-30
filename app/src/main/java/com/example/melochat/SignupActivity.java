package com.example.melochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup";

    private Spinner ageSpinner;
    private Button signupButton;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    public ArrayList<PostItem> postsList;
    private DatabaseReference postsDatabase;
    private RecyclerView recyclerView;
    private PostRVAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;
    private StorageReference mStorage;
    StorageReference profileImagesRef;

    int SELECT_PICTURE = 200;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Storage
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        profileImagesRef = mStorage.child("profileImages");

        // Initialize Edit Texts
        nameText = findViewById(R.id.editText_name);
        emailText = findViewById(R.id.editText_email);
        passwordText = findViewById(R.id.editText_password);
        // Initialize Image View
        profileImageView = findViewById(R.id.imageView_profilePic);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(view);
            }
        });
        // Initialize Age Spinner
        ageSpinner = (Spinner) findViewById(R.id.spinner_age);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.age_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);
        // Initialize Signup Button
        signupButton = findViewById(R.id.button_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if photo has been selected
                if (imageUri != null) {
                    createAccount(emailText.getText().toString(),
                            passwordText.getText().toString(),
                            nameText.getText().toString(),
                            imageUri);
                }
                else {
                    imageUri = Uri.parse("android.resource://com.example.melochat/drawable/profile64.png");
                    createAccount(emailText.getText().toString(),
                            passwordText.getText().toString(),
                            nameText.getText().toString(),
                            imageUri);
                }
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

    // Reference: https://firebase.google.com/docs/auth/android/password-auth
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    // Reference: https://firebase.google.com/docs/auth/android/password-auth
    private void createAccount(String email, String password, String name, Uri imageUri) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Set display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // add photo to Storage Firebase naming it by userID
                            profileImagesRef.child(user.getUid()).putFile(imageUri);

                            // Display success message and bring user to Feed Page
                            Toast.makeText(SignupActivity.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, FeedActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Reference: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    public void uploadImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    // Reference: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    profileImageView.setImageURI(selectedImageUri);
                    imageUri = selectedImageUri;
                }
            }
        }
    }
}