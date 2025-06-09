package adar.dar.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OTPLogIn extends AppCompatActivity {

    // List of OTP input fields (EditTexts)
    List<EditText> otpBoxes = new ArrayList<>();

    String phoneNumber;
    Long timeoutSeconds = 60L; // OTP timeout duration
    Button next;
    boolean sentAgain = false;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView sendAgain;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.otp_log_in);

        // Adjust for system UI insets (like status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components and logic
        initView();
    }

    private void initView() {
        // Set up the OTP input boxes
        initBoxes();

        next = findViewById(R.id.ICbtn);
        next.setVisibility(View.GONE); // Hidden until OTP is sent

        progressBar = findViewById(R.id.OTIprogressBar);
        sendAgain = findViewById(R.id.OLIsend);

        // Get the phone number passed from previous activity
        phoneNumber = getIntent().getExtras().getString("phone");

        // Send the OTP for the first time
        sendOtp(phoneNumber, false);

        // Handle when "Next" is clicked (after entering OTP)
        next.setOnClickListener(v -> {
            String enteredOtp = "";
            for (EditText et : otpBoxes) {
                enteredOtp += et.getText().toString();
            }
            // Create credential using entered OTP and verification code
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
        });

        // Handle "Send again" click for resending OTP
        sendAgain.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);

            if (!sentAgain) {
                sendOtp(phoneNumber, true); // Resend OTP
                sentAgain = true;
            }
        });
    }

    // Sends OTP to the provided phone number
    void sendOtp(String phoneNumber, boolean isResend) {
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            // Called if verification is done automatically
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                            }

                            // Called if verification fails
                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Log.e("FirebaseAuth", "Verification failed", e);
                                Toast.makeText(getApplicationContext(), "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            // Called when the code is successfully sent
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Log.d("FirebaseAuth", "Code Sent: " + s);

                                // Save verification code and resending token
                                verificationCode = s;
                                resendingToken = forceResendingToken;

                                progressBar.setVisibility(View.GONE);
                                next.setVisibility(View.VISIBLE);
                            }
                        });

        // Build the request and either send or resend OTP
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    // Sign in using the provided PhoneAuthCredential
    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Go to account setup screen
                            Intent intent = new Intent(OTPLogIn.this, InitiateAccount.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                        } else {
                            // Invalid code
                            Toast.makeText(OTPLogIn.this, "The code is not correct", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    // Initialize and configure the 6-digit OTP input fields
    void initBoxes() {
        otpBoxes.add(findViewById(R.id.otp_digit_1));
        otpBoxes.get(0).requestFocus(); // Start with first box focused
        otpBoxes.add(findViewById(R.id.otp_digit_2));
        otpBoxes.add(findViewById(R.id.otp_digit_3));
        otpBoxes.add(findViewById(R.id.otp_digit_4));
        otpBoxes.add(findViewById(R.id.otp_digit_5));
        otpBoxes.add(findViewById(R.id.otp_digit_6));

        // Add text change listener to each box
        for (int i = 0; i < otpBoxes.size(); i++) {
            int index = i;
            otpBoxes.get(i).addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpBoxes.size() - 1) {
                        // Move to next box
                        otpBoxes.get(index + 1).requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0 && index > 0) {
                        // Move to previous box if character is deleted
                        otpBoxes.get(index - 1).requestFocus();
                    }
                }
            });
        }
    }
}
