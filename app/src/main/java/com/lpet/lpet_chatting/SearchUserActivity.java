package com.lpet.lpet_chatting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchUserInput;
    ImageButton searchUserBtn;
    ImageButton backBtn;
    RecyclerView searchUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchUserInput = findViewById(R.id.search_user_input);
        searchUserBtn = findViewById(R.id.search_user_btn);
        backBtn = findViewById(R.id.btn_back);
        searchUserList = findViewById(R.id.search_user_recycler_view);

        searchUserInput.requestFocus();
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        searchUserBtn.setOnClickListener(v -> {
            String searchTerm = searchUserInput.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchUserInput.setError("Por favor, ingrese un nombre de usuario válido.");
                return;
            }
            setupRecyclerView(searchTerm);
        });
    }

    private void setupRecyclerView(String searchTerm) {

    }
}