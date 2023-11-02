package com.lpet.lpet_chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.lpet.lpet_chatting.adapters.ChatRecyclerAdapter;
import com.lpet.lpet_chatting.adapters.SearchUserRecyclerAdapter;
import com.lpet.lpet_chatting.models.Chatroom;
import com.lpet.lpet_chatting.models.Message;
import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.AndroidUtil;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {
    User otherUser;
    Chatroom chatroom;
    String chatroomId;
    EditText etMessage;
    ImageButton sendButton;
    ImageButton backButton;
    TextView otherUserName;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;
    ImageView profilePic;

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
        profilePic = findViewById(R.id.profile_pic);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(uriTask -> {
                    if (uriTask.isSuccessful()) {
                        Uri downloadUri = uriTask.getResult();
                        AndroidUtil.setProfilePic(this, downloadUri, profilePic);
                    }
                });

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
        setupChatRecyclerView();
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessagesReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToChatroom(String message) {
        chatroom.setLastMessageTimestamp(Timestamp.now());
        chatroom.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroom.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroom);

        Message messageObject = new Message(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessagesReference(chatroomId).add(messageObject)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            etMessage.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    private void sendNotification(String message) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User currentUser = task.getResult().toObject(User.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", currentUser.getUsername());
                    notificationObject.put("body", message);

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObject);
                    jsonObject.put("data", dataObject);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        String key = "AAAA6F6j6DM:APA91bHPZDHH4C9Jw5v_fwlweaBgEgeDwbHTQYef1g1ED1sBC7dl10ZECUNHkkDTV7bKyiUzyqDGJD2CrBOqyGAGc85uL8fL8pBS6H7EHwWECdv8ApVeHVaQc0umA_9BlbE-CnywNJ4B";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + key)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Notification sent");
                } else {
                    System.out.println("Notification failed");
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
                            "",
                            "");
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroom);
                }
            }
        });
    }
}