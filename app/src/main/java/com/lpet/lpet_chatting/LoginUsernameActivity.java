package com.lpet.lpet_chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

public class LoginUsernameActivity extends AppCompatActivity {
    EditText usernameInput;
    Button enterBtn;
    ProgressBar progressBar;
    String phoneNumber;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.et_username);
        enterBtn = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        phoneNumber = getIntent().getStringExtra("phone");
        getUsername();

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUsername();
            }
        });
    }

    private void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    user = task.getResult().toObject(User.class);

                    if (user != null && user.getUsername() != null) {
                        usernameInput.setText(user.getUsername());
                    }
                }
            }
        });
    }

    private void setUsername() {
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("El nombre de usuario debe tener al menos 3 caracteres");
            usernameInput.requestFocus();
            return;
        }

        setInProgress(true);
        if (user != null) {
            user.setUsername(username);
        } else {
            user = new User(phoneNumber, username, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent i = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });
    }

    private void setInProgress(boolean isInProgress) {
        if (isInProgress) {
            progressBar.setVisibility(View.VISIBLE);
            enterBtn.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            enterBtn.setVisibility(View.VISIBLE);
        }
    }
}