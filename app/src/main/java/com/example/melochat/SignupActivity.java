package com.example.melochat;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup";

    private Spinner ageSpinner;
    private Button signupButton;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private ImageView profileImageView;
    private DatabaseReference database;
    private FirebaseAuth mAuth;

    int SELECT_PICTURE = 200;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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
                createAccount(emailText.getText().toString(),
                        passwordText.getText().toString(),
                        nameText.getText().toString());
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

    // Reference: https://firebase.google.com/docs/auth/android/password-auth
    private void createAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Set display name
                            // TODO set user profile photo
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                            //        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();
                            user.updateProfile(profileUpdates);

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

    public void uploadImage(View view) {
        //Intent intent = new Intent(Intent.ACTION_PICK,
         //       android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        /*
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 200);
        }

         */
    }


    // Reference: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout

                    profileImageView.setImageURI(selectedImageUri);
                }
            }
        }
    }
}