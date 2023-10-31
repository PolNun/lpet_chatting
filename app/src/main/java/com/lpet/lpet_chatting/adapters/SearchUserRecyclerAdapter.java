package com.lpet.lpet_chatting.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.lpet.lpet_chatting.ChatActivity;
import com.lpet.lpet_chatting.R;
import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.AndroidUtil;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User, SearchUserRecyclerAdapter.UserModelViewHolder> {
    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull User model) {
        holder.username.setText(model.getUsername());
        holder.phone.setText(model.getPhone());

        if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.username.setText(model.getUsername() + " (Vos)");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserAsIntent(i, model);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView phone;
        ImageView profilePic;

        public UserModelViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            phone = itemView.findViewById(R.id.phone_number);
            profilePic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
