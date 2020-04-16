package com.example.hfilapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser CurrentUser;

    private EditText countryCode;
    private EditText phoneNo;
    private Button gen_otp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        countryCode = findViewById(R.id.country_code_text);
        phoneNo = findViewById(R.id.phone_number_text);
        gen_otp = findViewById(R.id.generate_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUser = firebaseAuth.getCurrentUser();

        gen_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_code = countryCode.getText().toString();
                String phone_number = phoneNo.getText().toString();
                String complete_phone_number = "+" + country_code + phone_number;

                if (country_code.isEmpty() || phone_number.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in the form to continue.", Toast.LENGTH_SHORT).show();
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            60,
                            TimeUnit.SECONDS,
                            SignUpActivity.this,
                            mCallbacks
                    );

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(SignUpActivity.this, "Please Try Again!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent otpIntent = new Intent(SignUpActivity.this, OtpActivity.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        },
                        10000);

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(CurrentUser != null){
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();
                            // ...
                        } else {
                            task.getException();
                            Toast.makeText(SignUpActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void sendUserToHome() {

            Intent intent = new Intent(SignUpActivity.this,RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

    }
}
