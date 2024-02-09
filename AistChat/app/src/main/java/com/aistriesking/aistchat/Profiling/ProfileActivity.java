package com.aistriesking.aistchat.Profiling;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import static com.aistriesking.aistchat.AppConstants.PROFILE_IMAGE_URI_KEY;
import static com.aistriesking.aistchat.AppConstants.USER_DATA_PREF;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.aistriesking.aistchat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    NavigationView navigationView;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;

    private DatabaseReference mDatabase;

    private TextView usernameTextView, emailTV;
    private Button btnUpdateProfileImage;
    private ImageView circularProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling);

        usernameTextView = findViewById(R.id.usernameTextView);
        emailTV = findViewById(R.id.emailTextView);
        btnUpdateProfileImage = findViewById(R.id.btnUpdateProfileImage);
        circularProfile = findViewById(R.id.circular_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = mStorage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set click listener for the update profile image button
        btnUpdateProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Fetch and display user data
        fetchAndDisplayUserData();


        navigationView = findViewById(R.id.navigationprofiling);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Handle navigation item clicks
                int itemId = item.getItemId();
                if (itemId == R.id.act_dashboard) {
                    //startActivity(new Intent(ProfileActivity.this, Aistchat.class));
                    return true;
                }else if (itemId == R.id.act_ads) {
                    //startActivity(new Intent(ProfileActivity.this, ToulBot.class));

                    /*
                    We integrate AI ToulBot when you get back from meditation center Sir.
                    It will be good for us for a start.

                    Factors to highlight

                    We present and deploy Western Coaches on March


                    Deploy Aistchat when you get back
                    Add ToulBot
                    Aistries Tech moves to level heights

                    We move to Dubai.

                    So far so good the code base is easy capture.....

                    See you soon Buddy, and God Bless.!.

                    AistriesKing: 01/01/24
                    Our Year Of Greatness Has Arrived to Us.

                    */
                    return true;

                }
                return false;
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadImage(data.getData());
        }
    }

    private void uploadImage(Uri imageUri) {
        final StorageReference fileReference = mStorageRef.child("users").child(mAuth.getCurrentUser().getUid() + ".jpg");

        UploadTask uploadTask = fileReference.putFile(imageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                if (downloadUri != null) {
                    String imageUrl = downloadUri.toString();
                    saveImageUrlToFirestore(imageUrl);
                    updateProfileImage(imageUrl);
                    updateButtonText(); // Update the text of the button
                    updateSharedPreferences(imageUrl); // Update user data in SharedPreferences
                    Toast.makeText(getApplicationContext(), "Successful upload", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading image", e);
            Toast.makeText(getApplicationContext(), "Failed upload", Toast.LENGTH_LONG).show();
        });
    }

    private void updateProfileImage(String imageUrl) {
        // Use Picasso or Glide to load the image into the circularProfile ImageView
        Picasso.get().load(imageUrl).into(circularProfile);
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", imageUrl);

        mFirestore.collection("users").document(userId).collection("users")
                .add(imageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "Failed to save image URL", Toast.LENGTH_LONG).show();
                });


        SharedPreferences sharedPreferences = getSharedPreferences(USER_DATA_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMAGE_URI_KEY, imageUrl);
        editor.apply();
    }

    private void updateButtonText() {
        btnUpdateProfileImage.setText("Change Profile Image");
    }

    private void updateSharedPreferences(String imageUrl) {
        // Update user data in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_creds", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profile_image_url", imageUrl);
        editor.apply();
    }

    private void fetchAndDisplayUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            // Display user data
                            String userEmail = user.getEmail();
                            emailTV.setText("Email: " + userEmail);

                            // Load and display the profile image from SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("user_creds", MODE_PRIVATE);
                            String profileImageUrl = sharedPreferences.getString("profile_image_url", "");

                            String username = sharedPreferences.getString("username", "User");
                            String email = sharedPreferences.getString("email", "Email");

                            usernameTextView.setText("Welcome, " + username);
                            emailTV.setText("Email: " + email);
                            if (!profileImageUrl.isEmpty()) {
                                updateProfileImage(profileImageUrl);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data", databaseError.toException());
                }
            });
        }
    }
}
