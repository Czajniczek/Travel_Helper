package com.example.travelhelper.User;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserDatabase {

    //region INTERFACES
    public interface ProfileDataLoaded {
        void ProfileDataLoaded();
    }

    public interface ProfileImageLoaded {
        void ProfileImageLoaded(Uri uri);
    }
    //endregion

    //region VARIABLES
    //INTERFACES
    public ProfileDataLoaded profileDataLoaded;
    public ProfileImageLoaded profileImageLoaded;

    //FIREBASE
    private User user;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    //private FirebaseAuth firebaseAuth;

    //OTHERS
    //private String userId;
    private Context context;
    public static UserDatabase instance;
    //endregion

    //CONSTRUCTOR
    private UserDatabase(Activity myActivity, String userId) {
        this.context = myActivity.getApplicationContext();

        //this.userId = userId;
        //firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    //SINGLETON
    public static UserDatabase getInstance(Activity activity, String userId) {
        if (instance == null) instance = new UserDatabase(activity, userId);
        return instance;
    }

    public static void ClearInstance() {
        instance = null;
    }

    public void getUserFromFirebase(String userId) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
        user = new User(userId);

        //PROFILE IMAGE
        StorageReference profileRef = storageReference.child("Users/" + userId + "/User photo");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            user.setProfileImage(uri);
            if (profileImageLoaded != null) profileImageLoaded.ProfileImageLoaded(uri);
        }).addOnFailureListener(e -> {
            user.setProfileImage(Uri.parse("android.resource://" + context.getPackageName() + "/drawable/default_user_image"));
            if (profileImageLoaded != null) profileImageLoaded.ProfileImageLoaded(user.getProfileImage());
        });

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                user.setUserName(documentSnapshot.getString("Username"));
                user.setEmail(documentSnapshot.getString("E-mail"));
                user.setCity(documentSnapshot.getString("City"));
                user.setPhoneNumber(documentSnapshot.getString("Phone number"));
                user.setAccCreation(documentSnapshot.getString("Date of account creation"));
                if (profileDataLoaded != null) profileDataLoaded.ProfileDataLoaded();
            }
        });
    }

    public User getUser() {
        return user;
    }
}
