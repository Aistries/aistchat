package com.aistriesking.aistchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class SignupActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView logintv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        logintv = findViewById(R.id.loginTV);
        emailEditText = findViewById(R.id.emailET);
        passwordEditText = findViewById(R.id.passwordET);
        signupButton = findViewById(R.id.signupButton);

        logintv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // Handle empty email or password, show an error message, etc.
                    emailEditText.setError("Retry");
                    passwordEditText.setError("Retry");
                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                } else {

                    // Create a new user in Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // User registration successful
                                    String userId = mAuth.getCurrentUser().getUid();
                                    @SuppressLint("RestrictedApi") User user = new User(email);
                                    mDatabase.child("users").child(userId).setValue(user);
                                    // Handle success
                                    Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_LONG).show();

                                    saveUserData(email);
                                    openMain();
                                } else {
                                    // Handle failure
                                    Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    private void saveUserData(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_creds", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("email", email);
        editor.apply();
    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
