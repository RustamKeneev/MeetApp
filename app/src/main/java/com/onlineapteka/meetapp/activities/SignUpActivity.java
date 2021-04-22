package com.onlineapteka.meetapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.utilities.Constants;
import com.onlineapteka.meetapp.utilities.PreferenceManager;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private MaterialButton buttonSignUp;
    private ProgressBar progressBarSignUp;

    private FirebaseFirestore firebaseFirestore;

    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.image_back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.text_sign_in).setOnClickListener(v -> {onBackPressed();});
        editFirstName = findViewById(R.id.edit_first_name);
        editLastName = findViewById(R.id.edit_last_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        buttonSignUp = findViewById(R.id.button_sign_up);
        progressBarSignUp = findViewById(R.id.progress_bar_sign_up);

        preferenceManager = new PreferenceManager(getApplicationContext());

        buttonSignUp.setOnClickListener(v -> {
            if (editFirstName.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this,"Enter first name",Toast.LENGTH_LONG).show();
            }else if (editLastName.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this,"Enter last name",Toast.LENGTH_LONG).show();
            }else if (editEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this,"Enter email",Toast.LENGTH_LONG).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()){
                Toast.makeText(SignUpActivity.this,"Enter valid email",Toast.LENGTH_LONG).show();
            }else if (editPassword.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this,"Enter password",Toast.LENGTH_LONG).show();
            }else if (editConfirmPassword.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this,"Enter confirm password",Toast.LENGTH_LONG).show();
            }else if (!editPassword.getText().toString().equals(editConfirmPassword.getText().toString())){
                Toast.makeText(SignUpActivity.this,"Password and confirm password must be same",
                        Toast.LENGTH_LONG).show();
            }else {
                signUp();
            }
        });
    }
    private void signUp(){
        buttonSignUp.setVisibility(View.INVISIBLE);
        progressBarSignUp.setVisibility(View.VISIBLE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME,editFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME,editLastName.getText().toString());
        user.put(Constants.KEY_EMAIL,editEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,editPassword.getText().toString());

        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, editFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, editLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, editEmail.getText().toString());
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    progressBarSignUp.setVisibility(View.INVISIBLE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUpActivity.this,"Error " + e.getMessage(),Toast.LENGTH_LONG).show();
                });
    }
}