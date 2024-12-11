package com.example.a2fa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


public class SignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText;
    private Button signUpButton, loginButton;
    private DB DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        DB = new DB(this);

        // Initialize elements
        firstNameEditText = findViewById(R.id.editTextFirstname);
        lastNameEditText = findViewById(R.id.editTextLastname);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        signUpButton = findViewById(R.id.signUp);
        loginButton = findViewById(R.id.loginButton);

        signUpButton.setOnClickListener(v -> {
            if (validateFields()) {
                createAccount();
            }
        });

        loginButton.setOnClickListener(v -> {
            // Intent to navigate to the Login screen
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(firstNameEditText.getText().toString())) {
            firstNameEditText.setError("Please enter your first name");
            return false;
        }

        if (TextUtils.isEmpty(lastNameEditText.getText().toString())) {
            lastNameEditText.setError("Please enter your last name");
            return false;
        }

        if (TextUtils.isEmpty(emailEditText.getText().toString()) ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
            emailEditText.setError("Enter a valid email");
            return false;
        }

        if (TextUtils.isEmpty(passwordEditText.getText().toString()) || passwordEditText.getText().toString().length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void createAccount() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (DB.checkEmail(email)) {
            Toast.makeText(this, "An account with this email already exists!", Toast.LENGTH_SHORT).show();
        }
        else {
            DB.insertUser(firstName, lastName, email, password);
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}