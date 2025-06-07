package adar.dar.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.User;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView usernameText;
    TextView phoneText;
    LinearLayout username;
    ImageButton logout;
    LinearLayout phone;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView profileImageView;
    private Uri photoUri;;
    ImageView selectImageButton;
    View view;
    TextView Date;
    User user;


    ActivityResultLauncher<Intent> imagePicker;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    photoUri = data.getData();
                    setProfilePic(getContext(),photoUri,profileImageView);
                    if(photoUri!=null)
                    {
                       FireBaseUtil.GetCurrentProfileReference().putFile(photoUri);
                    }
                }
            }
        });
    }
    public static void setProfilePic(Context context, Uri imageURi, ImageView imageView)
    {
        Glide.with(context).load(imageURi).apply(RequestOptions.circleCropTransform()).into(imageView);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_profile, container, false);

        initViews();
        return view;
    }

    private void initViews() {
        usernameText=view.findViewById(R.id.FPuserName);
        phoneText=view.findViewById(R.id.FPphone);
        logout=view.findViewById(R.id.FPlogout);
        username=view.findViewById(R.id.FBuserLinear);
        profileImageView = view.findViewById(R.id.FPprofile_image);
        selectImageButton = view.findViewById(R.id.FPselect_image_button);
        phone=view.findViewById(R.id.FBphoneLinear);
        Date=view.findViewById(R.id.FPdate);
        SetUserData();
        username.setOnClickListener(this);
        logout.setOnClickListener((view) -> {
            LogoutDialog();

        });


        selectImageButton.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(720).maxResultSize(720,720).createIntent(new Function1<Intent, Unit>() {
                @Override
                public Unit invoke(Intent intent) {
                    imagePicker.launch(intent);

                    return null;
                }
            });
        });

    }

    private void LogoutDialog() {
        AlertDialog dialog=new AlertDialog.Builder(view.getContext()).setTitle("Log out").setMessage("Are you sure you want to log out? ").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        FireBaseUtil.logout();
                        Intent intent=new Intent(view.getContext(), SplashMainActivity.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

                dialog.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        dialog.show();
    }

    private void SetUserData() {

        FireBaseUtil.GetCurrentProfileReference().getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Uri uri= task.getResult();
                setProfilePic(getContext(),uri,profileImageView);
            }


        });
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            user=task.getResult().toObject(User.class);
            usernameText.setText(user.getUsername());
            phoneText.setText(user.getPhone());
            Date.setText(FireBaseUtil.TimeStampToTime(user.getCreatedTimestamp()));
        });
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Change Name");

        View dialogView = getLayoutInflater().inflate(R.layout.change_username_dialog, null);
        builder.setView(dialogView);

        EditText userName2 = dialogView.findViewById(R.id.CUDusername);
        userName2.setText(usernameText.getText());
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String Name=userName2.getText().toString();
                if(Name.isEmpty() || Name.length()<2){
                    userName2.setError("Username length should be at least 3 characters");

                }
                else if( Name.length()>12){
                    userName2.setError("Username length should be less than 12 characters");

                }
                else{
                    user.setUsername(Name);
                    FireBaseUtil.currentUserDetails().set(user);
                    dialog.dismiss();
                    SetUserData();

                }




            }

        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



}