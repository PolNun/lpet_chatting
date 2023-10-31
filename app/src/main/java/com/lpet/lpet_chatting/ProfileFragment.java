package com.lpet.lpet_chatting;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.AndroidUtil;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText etUsername;
    EditText etPhone;
    Button btnUpdateProfile;
    ProgressBar progressBar;
    TextView tvLogout;
    User currentUser;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image);
        etUsername = view.findViewById(R.id.et_username);
        etPhone = view.findViewById(R.id.et_phone_number);
        btnUpdateProfile = view.findViewById(R.id.btn_profile_update);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        tvLogout = view.findViewById(R.id.btn_logout);

        getUserdata();

        btnUpdateProfile.setOnClickListener(v -> {
            onBtnUpdateProfileClick();
        });

        tvLogout.setOnClickListener(v -> {
            FirebaseUtil.logout();
            Intent i = new Intent(getContext(), SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        return view;
    }

    private void onBtnUpdateProfileClick() {
        String newUsername = etUsername.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            etUsername.setError("Username cannot be empty");
            return;
        }
        setInProgress(true);
        currentUser.setUsername(newUsername);
        updateToFirestore();
    }

    private void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUser)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        etUsername.setText(currentUser.getUsername());
                        etPhone.setText(currentUser.getPhone());
                        AndroidUtil.showToast(getContext(), "Perfil actualizado");
                    } else {
                        AndroidUtil.showToast(getContext(), "Error al actualizar el perfil");
                    }
                });
    }

    private void getUserdata() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUser = task.getResult().toObject(User.class);
            assert currentUser != null;
            etUsername.setText(currentUser.getUsername());
            etPhone.setText(currentUser.getPhone());
            setInProgress(false);
        });
    }

    private void setInProgress(boolean isInProgress) {
        if (isInProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnUpdateProfile.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            btnUpdateProfile.setVisibility(View.VISIBLE);
        }
    }
}