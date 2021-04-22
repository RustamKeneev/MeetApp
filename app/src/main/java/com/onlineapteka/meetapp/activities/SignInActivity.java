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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.utilities.Constants;
import com.onlineapteka.meetapp.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar progressBarSignIn;

    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
    }

    private void initViews() {
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        buttonSignIn = findViewById(R.id.button_sign_in);
        progressBarSignIn = findViewById(R.id.progress_bar_sign_in);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        buttonSignIn.setOnClickListener(v -> {
            if (editEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(SignInActivity.this,"Enter email",Toast.LENGTH_LONG).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()){
                Toast.makeText(SignInActivity.this,"Enter valid email",Toast.LENGTH_LONG).show();
            }else if (editPassword.getText().toString().trim().isEmpty()){
                Toast.makeText(SignInActivity.this,"Enter password",Toast.LENGTH_LONG).show();
            }else {
                signIn();
            }
        });

        findViewById(R.id.text_sign_up).setOnClickListener(v -> startActivity(
                new Intent(getApplicationContext(),SignUpActivity.class)));
    }

    private void signIn(){
        buttonSignIn.setVisibility(View.INVISIBLE);
        progressBarSignIn.setVisibility(View.VISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,editEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,editPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() !=null && task.getResult().getDocuments().size() >0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        progressBarSignIn.setVisibility(View.INVISIBLE);
                        buttonSignIn.setVisibility(View.VISIBLE);
                        Toast.makeText(SignInActivity.this,"Unable to sign in",Toast.LENGTH_LONG).show();
                    }
                });
    }
}