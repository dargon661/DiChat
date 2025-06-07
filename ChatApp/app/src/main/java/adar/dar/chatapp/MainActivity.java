package adar.dar.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

import adar.dar.chatapp.Model.FireBaseUtil;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ChatFragment chatFragment;
    SearchUsersFragment searchUsersFragment;

    ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        InitViews();
    }

    private void InitViews() {
        GetFCMToken();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        searchUsersFragment=new SearchUsersFragment();
        profileFragment=new ProfileFragment();
        chatFragment=new ChatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,chatFragment).commit();
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chats)
                {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
                    if (currentFragment != null) {
                        transaction.remove(currentFragment);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_settings)
                {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
                    if (currentFragment != null) {
                        transaction.remove(currentFragment);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,profileFragment).commit();
                }
                if(item.getItemId()==R.id.menu_search)
                {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
                    if (currentFragment != null) {
                        transaction.remove(currentFragment);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,searchUsersFragment).commit();
                }
                return true;
            }
        });


    }

    private void GetFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();

                FireBaseUtil.currentUserDetails().update("fcmtoken", token);

            } else {
                Log.e("fcm", "Failed to get token");
            }
        });
    }



}