package com.lpet.lpet_chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchBtn;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBtn = findViewById(R.id.btn_search);

        searchBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_toolbar, chatFragment).commit();
            } else if (item.getItemId() == R.id.menu_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_toolbar, profileFragment).commit();
            }

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }
}