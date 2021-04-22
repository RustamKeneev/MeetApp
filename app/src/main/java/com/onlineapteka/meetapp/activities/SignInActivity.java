package com.onlineapteka.meetapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineapteka.meetapp.R;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.text_sign_up).setOnClickListener(v -> startActivity(
                new Intent(getApplicationContext(),SignUpActivity.class)));
        firebaseFirestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> user  = new HashMap<>();
        user.put("first_name","Rustam");
        user.put("last_name","Keneev");
        user.put("email","rustamkeneev@gmail.com");
        firebaseFirestore.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(SignInActivity.this,"User Inserted",Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(SignInActivity.this,"Error adding user",Toast.LENGTH_LONG).show());
    }
}