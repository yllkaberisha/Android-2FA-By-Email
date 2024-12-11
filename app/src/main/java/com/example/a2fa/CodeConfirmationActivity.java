package com.example.a2fa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;

public class CodeConfirmationActivity extends AppCompatActivity {

    private EditText editTextConfirmationCode;
    private String email, generatedOtp;
    private Button verifyButton, resendCodeButton;
    private DB DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_confirmation);
        DB = new DB(this);

        // Initialize the elements
        editTextConfirmationCode = findViewById(R.id.editTextConfirmationCode);

        verifyButton = findViewById(R.id.verifyButton);
        resendCodeButton = findViewById(R.id.resendCodeButton);

        email = getIntent().getStringExtra("email");

        sendCode(email);

        verifyButton.setOnClickListener(v -> {
            verifyCode(email);
        });

        resendCodeButton.setOnClickListener(v -> {
            sendCode(email);
        });
    }

    private void verifyCode(String email) {
        String code = editTextConfirmationCode.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(this, "Please enter the confirmation code", Toast.LENGTH_SHORT).show();
        } else if (code.equals(generatedOtp)){
            Intent intent = new Intent(CodeConfirmationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            Toast.makeText(this, "Invalid confirmation code", Toast.LENGTH_SHORT).show();

        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    private void sendEmail(String email, String otp) {
        new Thread(() -> {
            if (OTPEmailSender.sendEmail(email, otp)) {
                runOnUiThread(() -> Toast.makeText(this, "Confirmation code sent to your email", Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Failed to send confirmation code. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void sendCode(String email) {
        generatedOtp = generateOtp();
        DB.insertCodeConfirmation(email, generatedOtp);
        sendEmail(email, generatedOtp);

    }
}
