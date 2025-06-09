package adar.dar.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.User;

public class SplashMainActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_main);

        // Adjust padding based on system bars (e.g., notch, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get the currently logged-in Firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Allow network operations on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // If user is logged in and activity was launched with a userId (e.g., via notification)
        if (currentUser != null && getIntent().getExtras() != null) {
            String userID = getIntent().getExtras().getString("userId");

            // Fetch the user document from Firestore
            FireBaseUtil.allUserCollectionReference().document(userID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Convert Firestore document to User model
                    User userModel = task.getResult().toObject(User.class);

                    // Launch MainActivity without animation
                    Intent intentMain = new Intent(this, MainActivity.class);
                    intentMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentMain);

                    // Launch ChatActivity with user data
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("userName", userModel.getUsername());
                    intent.putExtra("userPhone", userModel.getPhone());
                    intent.putExtra("userImage", userModel.getProfileImageUrl());
                    intent.putExtra("Timestamp", userModel.getCreatedTimestamp());
                    intent.putExtra("userID", userModel.getUserId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // Close SplashMainActivity
                    finish();
                }
            });
        } else {
            // User is logged in but no userId was passed – go to MainActivity
            if (currentUser != null) {
                startActivity(new Intent(SplashMainActivity.this, MainActivity.class));
            } else {
                // No user is logged in – wait 3 seconds, then go to PhoneLogInActivity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashMainActivity.this, PhoneLogInActivity.class));
                        finish();
                    }
                }, 3000);
            }
        }
    }
}
