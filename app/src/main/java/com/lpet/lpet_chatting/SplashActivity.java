package com.lpet.lpet_chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.AndroidUtil;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUsersCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent i = new Intent(this, ChatActivity.class);
                            AndroidUtil.passUserAsIntent(i, user);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    });
        } else {
            new Handler().postDelayed(() -> {
                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }, 1000);
        }
    }
}