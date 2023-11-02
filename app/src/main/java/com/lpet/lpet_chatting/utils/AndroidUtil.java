package com.lpet.lpet_chatting.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
