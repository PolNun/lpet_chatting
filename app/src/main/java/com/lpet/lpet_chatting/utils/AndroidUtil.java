package com.lpet.lpet_chatting.utils;

import android.content.Context;
import android.widget.Toast;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.auth.User;

public class AndroidUtil {

    public static  void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, com.lpet.lpet_chatting.models.User model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
    }


    public static com.lpet.lpet_chatting.models.User getUserModelFromIntent(Intent intent) {
        com.lpet.lpet_chatting.models.User user = new com.lpet.lpet_chatting.models.User();
        user.setUsername(intent.getStringExtra("username"));
        user.setPhone(intent.getStringExtra("phone"));
        user.setUserId(intent.getStringExtra("userId"));
        user.setFcmToken(intent.getStringExtra("fcmToken"));
        return user;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}