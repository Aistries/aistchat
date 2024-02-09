package com.aistriesking.aistchat.UserComments;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aistriesking.aistchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    private EditText commentEditText;
    private Button addCommentButton;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;

    private int imageItemPosition;

    private FirebaseFirestore mFirestore;

    private static final String COMMENTS_COLLECTION = "comments";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Retrieve the image item position from the intent
        imageItemPosition = getIntent().getIntExtra("image_item_position", -1);

        comments = getIntent().getParcelableArrayListExtra("comments");

        // Ensure comments list is initialized
        if (comments == null) {
            comments = new ArrayList<>();
        }

        // Set up the RecyclerView to display comments
        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter((ArrayList<Comment>) comments);
        recyclerView.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();

        // Handle new comment submission
        addCommentButton.setOnClickListener(v -> addComment());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addComment() {
        String newCommentText = commentEditText.getText().toString();

        if (!newCommentText.isEmpty()) {
            // Retrieve the image URL from the intent
           /* String imageUrl = getIntent().getStringExtra("image_url");

            if (imageUrl != null) {
                // Add the new comment to Firebase
                //saveCommentToFirestore(newCommentText, imageUrl);
            } else {
                Toast.makeText(this, "Image URL not available", Toast.LENGTH_SHORT).show();
                return;
            }*/

            // Add the new comment to the list
            Comment newComment = new Comment("Recent post:" + newCommentText);
            comments.add(newComment);

            // Notify the adapter about the data change
            commentAdapter.notifyDataSetChanged();

            // Update the comment count in the MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("image_item_position", imageItemPosition);
            resultIntent.putParcelableArrayListExtra("comments", new ArrayList<>(comments));
            resultIntent.putExtra("comment_count", comments.size());

            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void saveCommentToFirestore(String commentText, String imageUrl) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Comment comment = new Comment(userId, commentText);

        // Extract the document path from the URL
        String documentPath = getImageDocumentPath(imageUrl);

        // Add the new comment to the Firestore collection
        mFirestore.collection(documentPath).add(comment)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Comment added with ID: " + documentReference.getId());

                    // Update comment count in the MainActivity
                    //fetchCommentsAndUpdateCount(imageUrl);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding comment", e);
                    Toast.makeText(getApplicationContext(), "Failed to add comment", Toast.LENGTH_LONG).show();
                });
    }*/

    /*private String getImageDocumentPath(String imageUrl) {
        // Extract the document path from the URL
        // Example URL: "https://firebasestorage.googleapis.com/v0/b/aistries-921f1.appspot.com/o/users%2F1704258235370.jpg?alt=media&token=894edb3d-57e5-4201-a147-d0240e94cab8"
        // Document path: "users/1704258235370.jpg"

        // Split the URL by "/"
        String[] parts = imageUrl.split("/");

        // Check if the URL contains the "o" segment
        int oIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if ("o".equals(parts[i])) {
                oIndex = i;
                break;
            }
        }

        // If "o" is found, extract the path after "o"
        if (oIndex != -1 && oIndex < parts.length - 1) {
            StringBuilder documentPathBuilder = new StringBuilder();
            for (int i = oIndex + 1; i < parts.length; i++) {
                documentPathBuilder.append(parts[i]);
                if (i < parts.length - 1) {
                    documentPathBuilder.append("/");
                }
            }
            return documentPathBuilder.toString();
        } else {
            // Handle the case where "o" is not found
            return null;
        }
    }



    private void fetchCommentsAndUpdateCount(String imageUrl) {
        // Construct the document path from the image URL
        String documentPath = getImageDocumentPath(imageUrl);

        // Fetch comments using the constructed document path
        mFirestore.collection(documentPath).document(imageUrl).collection(COMMENTS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Comment> comments = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            if (comment != null) {
                                comments.add(comment);
                            }
                        }

                        // Update comment count in the MainActivity
                        updateCommentCount(comments.size());
                    } else {
                        Log.e(TAG, "Error fetching comments: " + task.getException());
                    }
                });
    }*/


    private void updateCommentCount(int commentCount) {
        // Implement logic to update comment count in your MainActivity
        // You may broadcast an event or use other mechanisms to notify MainActivity
    }
}
