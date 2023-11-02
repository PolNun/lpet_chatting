package com.lpet.lpet_chatting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lpet.lpet_chatting.models.User;
import com.lpet.lpet_chatting.utils.AndroidUtil;
import com.lpet.lpet_chatting.utils.FirebaseUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText etUsername;
    EditText etPhone;
    Button btnUpdateProfile;
    ProgressBar progressBar;
    TextView tvLogout;
    User currentUser;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                selectedImageUri = data.getData();
                AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
            }
        });
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
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUtil.logout();
                    Intent i = new Intent(getContext(), SplashActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

        });

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
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

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        } else {
            updateToFirestore();
        }
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

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(), downloadUri, profilePic);
                    }
                });

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