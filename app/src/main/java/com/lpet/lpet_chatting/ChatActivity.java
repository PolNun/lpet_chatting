package com.lpet.lpet_chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.lpet.lpet_chatting.models.Chatroom;
import com.lpet.lpet_chatting.models.Message;
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

        sendButton.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (message.isEmpty()) return;

            sendMessageToChatroom(message);
        });

        getOrCreateChatRoom();
    }

    private void sendMessageToChatroom(String message) {
        chatroom.setLastMessageTimestamp(Timestamp.now());
        chatroom.setLastMessageSenderId(FirebaseUtil.currentUserId());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroom);

        Message messageObject = new Message(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessagesReference(chatroomId).add(messageObject)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            etMessage.setText("");
                        }
                    }
                });
    }

    private void getOrCreateChatRoom() {
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