package com.example.travelhelper.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelhelper.Dialogues.DeleteAccountDialog;
import com.example.travelhelper.LoginAndRegistration.LoginActivity;
import com.example.travelhelper.R;
//import com.example.travelhelper.User.User;
//import com.example.travelhelper.User.UserDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {

    //region VARIABLES
    //LAYOUT
    private Toolbar myToolbar;
    private ImageView profileImage;
    private EditText userName;
    private TextView accCreate;
    //private TextInputLayout mUserEmail;
    private TextInputLayout city, mPhoneNumber;
    private Button saveButton;

    //FIREBASE
    private User user;
    private DeleteAccountDialog deleteAccountDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private UserDatabase userDatabase;
    private StorageReference fileRef;
    private Boolean dataLoadedFlag = false;
    private Uri resultUri;

    //OTHERS
    private Activity myActivity;
    private Context myContext;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myActivity = getActivity();
        myContext = myActivity.getApplicationContext();

        myToolbar = myActivity.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) myActivity).setSupportActionBar(myToolbar);
        ((AppCompatActivity) myActivity).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        //HOOKS
        profileImage = view.findViewById(R.id.user_profile_user_image);
        userName = view.findViewById(R.id.user_profile_user_name);
        accCreate = view.findViewById(R.id.user_profile_date_of_account_create);
        //mUserEmail = view.findViewById(R.id.user_profile_email);
        city = view.findViewById(R.id.user_profile_city);
        mPhoneNumber = view.findViewById(R.id.user_profile_phone_number);
        saveButton = view.findViewById(R.id.user_profile_save_button);

        setHasOptionsMenu(true);

        //region TEXT CHANGE LISTENERS
        /*mUserEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        city.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidateCity();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPhoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ValidatePhoneNumber();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion

        //region ON CLICK LISTENERS
        profileImage.setOnClickListener(view1 -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.OVAL)
                .start(getActivity()));

        saveButton.setOnClickListener(v -> {

            /*if (!ValidateEmail() | !ValidateCity() | !ValidatePhoneNumber()) {
                if (!ValidateEmail()) {
                    mUserEmail.requestFocus();
                    return;
                } else if (!ValidateCity()) {
                    city.requestFocus();
                    return;
                } else {
                    mPhoneNumber.requestFocus();
                    return;
                }
            }*/

            if (!ValidateCity() | !ValidatePhoneNumber()) {
                if (!ValidateCity()) city.requestFocus();
                else mPhoneNumber.requestFocus();
                return;
            }

            user.setUserName(userName.getText().toString());
            //user.setEmail(mUserEmail.getEditText().getText().toString());
            user.setCity(city.getEditText().getText().toString());
            user.setPhoneNumber(mPhoneNumber.getEditText().getText().toString());
            UpdateUserDataFirebase();
        });
        //endregion

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_profile_logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.user_menu_logout) {
            firebaseAuth.signOut();
            UserDatabase.ClearInstance();
            startActivity(new Intent(myContext, LoginActivity.class));
            myActivity.finish();
            return true;
        } else if (id == R.id.user_menu_thrash_can) {
            deleteAccountDialog = new DeleteAccountDialog(myActivity);
            deleteAccountDialog.StartDeleteAccountDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!dataLoadedFlag) {
            userDatabase = UserDatabase.getInstance(myActivity, firebaseAuth.getUid());
            userDatabase.getUserFromFirebase(firebaseAuth.getUid());
            userDatabase.profileImageLoaded = this::SetProfileImage;
            userDatabase.profileDataLoaded = this::SetUserInformation;
        } else dataLoadedFlag = false;
    }

    private void SetUserInformation() {

        user = userDatabase.getUser();

        userName.setText(user.getUserName());
        //mUserEmail.getEditText().setText(user.getEmail());
        mPhoneNumber.getEditText().setText(user.getPhoneNumber());
        city.getEditText().setText(user.getCity());
        accCreate.setText(user.getAccCreation());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                dataLoadedFlag = true;
                SetProfileImage(resultUri);
                user.setProfileImage(resultUri);
                LoadUserProfileImageToFirebase();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), getResources().getString(R.string.toast_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void SetProfileImage(Uri imageUri) {
        if (getActivity() == null) return;
        Glide.with(getActivity()).load(imageUri).placeholder(R.drawable.ic_account).error(R.drawable.ic_account).into(profileImage);
    }

    private void LoadUserProfileImageToFirebase() {

        fileRef = firebaseStorage.getReference().child("Users/" + firebaseAuth.getUid() + "/User photo");
        fileRef.putFile(user.getProfileImage()).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(myContext, getResources().getString(R.string.toast_profile_photo_changed), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(myContext, getResources().getString(R.string.toast_profile_photo_error), Toast.LENGTH_LONG).show();
        });
    }

    public void UpdateUserDataFirebase() {
        /*FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        user1.updateEmail(user.getEmail()).addOnSuccessListener(task -> {
                Toast.makeText(myContext, "Udało się kurwa", Toast.LENGTH_LONG).show();
        });*/

        /*FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user2.getEmail(),user2.get)*/

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(user.getId());

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("Username", user.getUserName());
        userMap.put("E-mail", user.getEmail());
        userMap.put("City", user.getCity());
        userMap.put("Phone number", user.getPhoneNumber());

        documentReference.update(userMap).addOnCompleteListener(task -> {
            Toast.makeText(getContext(), getResources().getString(R.string.toast_data_has_been_updated), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(getContext(), getResources().getString(R.string.toast_error_during_data_saving), Toast.LENGTH_LONG).show());
    }

    //region VALIDATION
    /*private boolean ValidateEmail() {
        String email = mUserEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mUserEmail.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mUserEmail.setError(getString(R.string.email_validate_error));
            return false;
        } else {
            mUserEmail.setError(null);
            return true;
        }
    }*/

    private boolean ValidateCity() {
        String mCity = city.getEditText().getText().toString().trim();

        if (mCity.isEmpty()) {
            city.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (!mCity.matches(".{3,}")) {
            city.setError(getString(R.string.wrong_city_name));
            return false;
        } else {
            city.setError(null);
            return true;
        }
    }

    private boolean ValidatePhoneNumber() {
        String phone = mPhoneNumber.getEditText().getText().toString().trim();

        if (phone.isEmpty()) {
            mPhoneNumber.setError(getString(R.string.field_can_not_be_empty_error));
            return false;
        } else if (phone.length() != 9) {
            mPhoneNumber.setError(getString(R.string.wrong_phone_number));
            return false;
        } else {
            mPhoneNumber.setError(null);
            return true;
        }
    }
    //endregion
}