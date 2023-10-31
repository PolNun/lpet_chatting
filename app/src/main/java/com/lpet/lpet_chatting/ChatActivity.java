package com.lpet.lpet_chatting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.lpet.lpet_chatting.models.Chatroom;
import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.AndroidUtil;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    User otherUser;
    Chatroom chatroom;
    String chatroomId;
    EditText etMessage;
    ImageButton sendButton;
    ImageButton backButton;
    TextView otherUserName;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        etMessage = findViewById(R.id.et_msg);
        sendButton = findViewById(R.id.btn_send);
        backButton = findViewById(R.id.btn_back);
        otherUserName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backButton.setOnClickListener((v) -> {
            onBackPressed();
        });

        otherUserName.setText(otherUser.getUsername());

        getOrCreateChatRoom();
    }

    void getOrCreateChatRoom() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroom = task.getResult().toObject(Chatroom.class);

                if (chatroom == null) {
                    chatroom = new Chatroom(chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),
                                    otherUser.getUserId()), Timestamp.now(),
                            "");
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroom);
                }
            }
        });
    }
}