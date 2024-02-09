package com.aistriesking.aistchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;

    private TextView registertv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        registertv = findViewById(R.id.registerTV);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Sign in with Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // User login successful
                                // Handle success
                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
                                saveUserData(email);
                                openMain();
                            } else {
                                // Handle failure
                                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        registertv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerfunction();
            }
        });
    }

    private void saveUserData(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_creds", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", email);
        editor.apply();
    }

    private void registerfunction() {
        Intent reg = new Intent(this, SignupActivity.class);
        startActivity(reg);
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
