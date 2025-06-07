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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPLogIn extends AppCompatActivity {

     List<EditText> otpBoxes = new ArrayList<>();
    String phoneNumber;
    Long timeoutSeconds = 60L;
    Button next;
    boolean sentAgain=false;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView sendAgain;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.otp_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        initBoxes();
        next=findViewById(R.id.ICbtn);
        next.setVisibility(View.GONE);
        progressBar=findViewById(R.id.OTIprogressBar);
        sendAgain=findViewById(R.id.OLIsend);
        phoneNumber = getIntent().getExtras().getString("phone");
        sendOtp(phoneNumber,false);
        next.setOnClickListener(v -> {
            String enteredOtp="";
            for(EditText et :  otpBoxes)
            {
                enteredOtp+=et.getText().toString();
            }
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
        });


        sendAgain.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
            if(!sentAgain)
            {
                sendOtp(phoneNumber,true);
                sentAgain=true;
            }
        });

    }


    void sendOtp(String phoneNumber,boolean isResend){
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                            }
                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                Log.e("FirebaseAuth", "Verification failed", e);
                                Toast.makeText(getApplicationContext(), "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);

                                Log.d("FirebaseAuth", "Code Sent: " + s);
                                // Store verificationId to verify code later
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                progressBar.setVisibility(View.GONE);
                                next.setVisibility(View.VISIBLE);
                            }

                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(OTPLogIn.this, InitiateAccount.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);
                } else {
                    Toast.makeText(OTPLogIn.this, "the code is not correct", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    void initBoxes() {
        otpBoxes.add(findViewById(R.id.otp_digit_1));
        otpBoxes.get(0).requestFocus();
        otpBoxes.add(findViewById(R.id.otp_digit_2));
        otpBoxes.add(findViewById(R.id.otp_digit_3));
        otpBoxes.add(findViewById(R.id.otp_digit_4));
        otpBoxes.add(findViewById(R.id.otp_digit_5));
        otpBoxes.add(findViewById(R.id.otp_digit_6));
        for (int i = 0; i < otpBoxes.size(); i++) {
            int index = i;
            otpBoxes.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Move to the next box if available
                        if (index < otpBoxes.size() - 1) {
                            otpBoxes.get(index + 1).requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        // Move to the previous box if available
                        if (index > 0) {
                            otpBoxes.get(index - 1).requestFocus();
                        }
                    }
                }
            });
        }
    }
}






