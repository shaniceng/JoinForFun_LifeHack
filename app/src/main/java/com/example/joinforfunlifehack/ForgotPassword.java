package com.example.joinforfunlifehack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener  {

    private TextView banner;
    private EditText emailEditText;
    private Button resetPasswordBtn;
    private ImageButton backBtn;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        banner = (TextView) findViewById(R.id.banner2);
        banner.setOnClickListener(this);
        emailEditText = (EditText) findViewById(R.id.editTextForgotPasswordEmail);
        resetPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
        resetPasswordBtn.setOnClickListener(this);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner2:
            case R.id.backBtn:
                startActivity(new Intent(this, LoginPage.class));
                break;
            case R.id.forgotPasswordBtn:
                resetPassword();
                break;

        }
    }
    
    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();
        
        if (email.isEmpty()) {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide a valid email address!");
            emailEditText.requestFocus();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ForgotPassword.this, "Try again! Something wrong happened.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        
    }


}