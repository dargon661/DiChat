package adar.dar.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.User;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class InitiateAccount extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView profileImageView;
    private Uri photoUri;
    private ProgressBar progressBar;
    private String phoneNumber;
    private User user;
    private EditText userName;
    private Button finishBtn;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    // Launcher to handle image picker result
    ActivityResultLauncher<Intent> imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_account);

        // Initialize image picker for profile image
        imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    photoUri = data.getData(); // Store selected image URI
                    setProfilePic(this, photoUri, profileImageView); // Load it into ImageView
                }
            }
        });

        InitViews(); // Initialize all UI elements
    }

    // Static helper method to set profile image with Glide
    public static void setProfilePic(Context context, Uri imageURi, ImageView imageView) {
        Glide.with(context)
                .load(imageURi)
                .apply(RequestOptions.circleCropTransform()) // Make image circular
                .into(imageView);
    }

    // Initialize all views and set click listeners
    private void InitViews() {
        phoneNumber = getIntent().getExtras().getString("phone");
        finishBtn = findViewById(R.id.ICbtn);
        progressBar = findViewById(R.id.ICprogressBar);
        progressBar.setVisibility(View.GONE);

        profileImageView = findViewById(R.id.profile_image);
        ImageView selectImageButton = findViewById(R.id.select_image_button);
        userName = findViewById(R.id.ICusername);

        finishBtn.setOnClickListener(this);

        // Open image picker on button click
        selectImageButton.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(720)
                    .maxResultSize(720, 720)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePicker.launch(intent);
                            return null;
                        }
                    });
        });

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        GetUserDetails(); // Load user profile info if it exists
    }

    // Handles click event on "Finish" button
    public void onClick(View v) {
        finishBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        SetUserName(); // Validate and set user name

        // Upload profile picture if it was selected
        if (photoUri != null) {
            uploadImageToFirebaseStorage(photoUri);
        }

        saveUserData(); // Save user data to Firestore
    }

    // Uploads profile picture to Firebase Storage
    private void uploadImageToFirebaseStorage(Uri photoUri) {
        if (photoUri != null) {
            FireBaseUtil.GetCurrentProfileReference().putFile(photoUri);
        }
    }

    // Validates and sets the user name in the `user` object
    void SetUserName() {
        String username = userName.getText().toString();
        if (username.isEmpty() || username.length() < 2) {
            userName.setError("Username length should be at least 3 characters");
            finishBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (username.length() > 12) {
            userName.setError("Username length should be less than 12 characters");
            finishBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (user != null) {
            user.setUsername(username);
        } else {
            // Create new user object
            user = new User(phoneNumber, username, Timestamp.now(), "", FireBaseUtil.currentUserId());
        }
    }

    // Saves user data (username, etc.) to Firestore
    private void saveUserData() {
        FireBaseUtil.currentUserDetails().set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                askForNotificationPermission(); // Proceed to next screen
            } else {
                finishBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InitiateAccount.this, "Error updating user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Ask for notification permission (for Android 13+)
    private void askForNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            goToMainActivity();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            goToMainActivity(); // Continue even if denied
        }
    }

    // Navigate to MainActivity and clear back stack
    private void goToMainActivity() {
        Intent intent = new Intent(InitiateAccount.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Load profile image and username from Firestore and Firebase Storage
    void GetUserDetails() {
        // Load profile picture
        FireBaseUtil.GetCurrentProfileReference().getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                setProfilePic(this, uri, profileImageView);
            }
        });

        // Load user details
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().toObject(User.class);
                    if (user != null) {
                        userName.setText(user.getUsername()); // Pre-fill username if already saved
                    }
                }
            }
        });
    }
}
