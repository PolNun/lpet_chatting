package com.lpet.lpet_chatting.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lpet.lpet_chatting.models.User;

public class AndroidUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserAsIntent(Intent i, User user) {
        i.putExtra("userId", user.getUserId());
        i.putExtra("username", user.getUsername());
        i.putExtra("phone", user.getPhone());
    }

    public static User getUserFromIntent(Intent i) {
        User user = new User();
        user.setUserId(i.getStringExtra("userId"));
        user.setUsername(i.getStringExtra("username"));
        user.setPhone(i.getStringExtra("phone"));
        return user;
    }
}
