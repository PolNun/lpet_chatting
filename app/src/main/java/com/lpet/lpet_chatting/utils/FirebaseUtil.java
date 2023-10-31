package com.lpet.lpet_chatting.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static Boolean isLoggedIn() {
        return currentUserId() != null;
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }


    public static CollectionReference allUsersCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String userId1, String userId2) {
        return userId1.hashCode() < userId2.hashCode() ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
}
