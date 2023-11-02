package com.lpet.lpet_chatting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.lpet.lpet_chatting.adapters.SearchUserRecyclerAdapter;
import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchUserInput;
    ImageButton searchUserBtn;
    ImageButton backBtn;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchUserInput = findViewById(R.id.search_user_input);
        searchUserBtn = findViewById(R.id.search_user_btn);
        backBtn = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        recyclerView.setItemAnimator(null);

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
        Query query = FirebaseUtil.allUsersCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + "\uf8ff");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.startListening();
    }
}