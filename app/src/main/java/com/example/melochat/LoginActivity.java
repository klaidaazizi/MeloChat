package com.example.melochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.melochat.models.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    public ArrayList<PostItem> postsList;
    private DatabaseReference postsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.editText_email);
        passwordText = findViewById(R.id.editText_password);

        loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if (email.isEmpty() & password.isEmpty()) {
                    Utils.postToastMessage("Enter valid email and password", view.getContext());
                }
                else if (email.isEmpty()) {
                    Utils.postToastMessage("Enter valid email", view.getContext());
                }
                else if (password.isEmpty()) {
                    Utils.postToastMessage("Enter valid password", view.getContext());
                }
                else {
                    signIn(email, password);
                }
            }
        });

        database = FirebaseDatabase.getInstance().getReference();
        postsDatabase = database.child("postsWithComments");
        postsList = new ArrayList<>();
        postsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatePosts(snapshot.getChildren());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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

    // Reference: https://github.com/firebase/snippets-android/blob/8fbcdaef064ed94d8ee9efc662c00991ff397254/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java#L85-L102
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                            intent.putExtra("posts", postsList);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Incorrect email or password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}