package com.aistriesking.aistchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aistriesking.aistchat.ChatUpdates.Notifications;
import com.aistriesking.aistchat.Groups.GroupChat;
import com.aistriesking.aistchat.Notify.NotificationsActivity;
import com.aistriesking.aistchat.Profiling.ProfileActivity;
import com.aistriesking.aistchat.UserChats.ChatActivity;
import com.aistriesking.aistchat.UserComments.CommentActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int COMMENT_REQUEST_CODE = 1;

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<ImageItem> imageItems;

    private ImageView profileimage;

    private FloatingActionButton floatingActionButton;

    NavigationView navigationView;

    private static final String USER_DATA_PREF = AppConstants.USER_DATA_PREF;
    private static final String PROFILE_IMAGE_URI_KEY = AppConstants.PROFILE_IMAGE_URI_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = mStorage.getReference();

        profileimage = findViewById(R.id.profileIV);

        initializeViews();

        SharedPreferences sharedPreferences = getSharedPreferences(USER_DATA_PREF, MODE_PRIVATE);
        String profileImageUri = sharedPreferences.getString(PROFILE_IMAGE_URI_KEY, null);

        if (profileImageUri != null) {
            updateProfileImage(profileImageUri);
        }

        // Fetch the image URLs for the RecyclerView
        fetchImageUrls();

        // Set up the Bottom Navigation View
        setUpBottomNavigationView();

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfilePage();
            }
        });
    }

    private void updateProfileImage(String imageUrl) {
        //progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(imageUrl).into(profileimage, new Callback() {
            @Override
            public void onSuccess() {
                //progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openProfilePage() {
        Intent profile = new Intent(this, ProfileActivity.class);
        startActivity(profile);
    }

    private void initializeViews() {
        Button uploadButton = findViewById(R.id.uploadButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageItems = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageItems, this::handleLikeAction, this::handleUnlikeAction, this::handleCommentAction);
        recyclerView.setAdapter(imageAdapter);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> chatActivity());

        uploadButton.setOnClickListener(v -> openFileChooser());
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageItems = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageItems, this::handleLikeAction, this::handleUnlikeAction, this::handleCommentAction);
        recyclerView.setAdapter(imageAdapter);
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                return true;
            } else if (itemId == R.id.nav_people) {
                startActivity(new Intent(MainActivity.this, GroupChat.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void chatActivity() {
        Intent chat = new Intent(this, ChatActivity.class);
        startActivity(chat);
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

        if (requestCode == COMMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("image_item_position", -1);
            if (position != -1) {
                List<String> updatedComments = data.getStringArrayListExtra("comments");
                ImageItem updatedItem = imageItems.get(position);
                updatedItem.setComments(updatedComments);

                // Update comment count
                TextView commentCountTextView = findViewById(R.id.commentCountTextView);
                if (commentCountTextView != null) {
                    commentCountTextView.setText(String.valueOf(updatedComments.size()));
                }

                imageAdapter.notifyItemChanged(position);
            }
        }

        // Fetch the updated profile image URL from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(USER_DATA_PREF, MODE_PRIVATE);
        String updatedProfileImageUri = sharedPreferences.getString(PROFILE_IMAGE_URI_KEY, null);

        if (updatedProfileImageUri != null) {
            updateProfileImage(updatedProfileImageUri);
        }

    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            final StorageReference fileReference = mStorageRef.child("users").child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveImageUrlToFirestore(imageUrl);
                            Toast.makeText(getApplicationContext(), "Successful upload", Toast.LENGTH_LONG).show();
                            // Fetch the image URLs for the RecyclerView
                            fetchImageUrls();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to upload
                        Log.e(TAG, "Error uploading image", e);
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Image URI is null", Toast.LENGTH_LONG).show();
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", imageUrl);

        mFirestore.collection("users").document(userId).collection("users")
                .add(imageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    ImageItem newItem = new ImageItem(imageUrl, 0, new ArrayList<>(), false);
                    imageItems.add(newItem);
                    imageAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "Failed upload", Toast.LENGTH_LONG).show();
                });
    }

    private void fetchImageUrls() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mFirestore.collection("users").document(userId).collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleFetchImageUrlsResult(task);
                    } else {
                        Log.e(TAG, "Error fetching images: " + task.getException());
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleFetchImageUrlsResult(@NonNull Task<QuerySnapshot> task) {
        imageItems.clear();
        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
            String imageUrl = document.getString("imageUrl");
            int likeCount = document.getLong("likeCount") != null ? Objects.requireNonNull(document.getLong("likeCount")).intValue() : 0;

            List<String> comments = new ArrayList<>();

            if (imageUrl != null) {
                ImageItem newItem = new ImageItem(imageUrl, likeCount, comments, false);
                imageItems.add(newItem);
            }
        }
        imageAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleLikeAction(int i, ImageButton likeButton, ImageButton unlikeButton, TextView textView) {
        ImageItem currentItem = imageItems.get(i);
        if (!currentItem.isLiked()) {
            currentItem.setLiked(true);
            currentItem.setLikeCount(currentItem.getLikeCount() + 1);
            updateLikeButtonUI(likeButton, unlikeButton, currentItem.getLikeCount());
        }
        imageAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleUnlikeAction(int i, ImageButton likeButton, ImageButton unlikeButton, TextView textView) {
        ImageItem currentItem = imageItems.get(i);
        if (currentItem.isLiked()) {
            currentItem.setLiked(false);
            currentItem.setLikeCount(currentItem.getLikeCount() - 1);
            updateLikeButtonUI(likeButton, unlikeButton, currentItem.getLikeCount());
        }
        imageAdapter.notifyItemChanged(i);
    }

    private void updateLikeButtonUI(ImageButton likeButton, ImageButton unlikeButton, int likeCount) {
        likeButton.setVisibility(View.GONE);
        unlikeButton.setVisibility(View.VISIBLE);

        TextView likeCountTextView = findViewById(R.id.likeCountTextView);
        if (likeCountTextView != null) {
            likeCountTextView.setText(String.valueOf(likeCount));
        }
    }

    private void handleCommentAction(int position) {
        ImageItem currentItem = imageItems.get(position);
        Intent commentIntent = new Intent(this, CommentActivity.class);
        commentIntent.putStringArrayListExtra("comments", new ArrayList<>(currentItem.getComments()));
        commentIntent.putExtra("image_item_position", position);
        startActivityForResult(commentIntent, COMMENT_REQUEST_CODE);
    }
}

