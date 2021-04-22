package com.onlineapteka.meetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.utilities.Constants;
import com.onlineapteka.meetapp.utilities.PreferenceManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        preferenceManager = new PreferenceManager(getApplicationContext());

        textTitle = findViewById(R.id.text_title);
        textTitle.setText(String.format("%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)));

        findViewById(R.id.text_sign_out).setOnClickListener(v -> {
            signOut();
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
           if (task.isSuccessful() &&  task.getResult() !=null){
               sendFCMTokenToDatabase(task.getResult().getToken());
           }
        });

    }
    private void sendFCMTokenToDatabase(String token){
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this,
                        "Token update successfully", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this,"Unable to send token " +
                                        e.getMessage(), Toast.LENGTH_LONG).show());
    }
    private void signOut(){
        Toast.makeText(MainActivity.this,"Sign Out...",Toast.LENGTH_LONG).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(aVoid -> {
           preferenceManager.clearPreference();
           startActivity(new Intent(getApplicationContext(),SignInActivity.class));
           finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this,"Unable to sign in out",Toast.LENGTH_LONG).show();
        });
    }
}