package com.onlineapteka.meetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.adapters.UsersAdapter;
import com.onlineapteka.meetapp.listeners.UserListeners;
import com.onlineapteka.meetapp.models.User;
import com.onlineapteka.meetapp.utilities.Constants;
import com.onlineapteka.meetapp.utilities.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.onlineapteka.meetapp.R.string.unable_to_sign_in_out;

public class MainActivity extends AppCompatActivity implements UserListeners {

    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private TextView textTitle;
    private TextView textErrorMessage;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<User> userList;
    private UsersAdapter usersAdapter;
    private ImageView imageConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        preferenceManager = new PreferenceManager(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerview_users);
        textErrorMessage = findViewById(R.id.text_error_message);
        textTitle = findViewById(R.id.text_title);
        imageConference = findViewById(R.id.image_conference);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

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

        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(userList,this);
        recyclerView.setAdapter(usersAdapter);

        getUsers();
    }

    private void getUsers(){
        swipeRefreshLayout.setRefreshing(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() !=null){
                        userList.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if (myUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.firstName = queryDocumentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.lastName = queryDocumentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            userList.add(user);
                        }
                        if (userList.size() > 0 ){
                            usersAdapter.notifyDataSetChanged();
                        }else {
                            textErrorMessage.setText(String.format("%s",getString(R.string.no_users_available)));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }else {
                        textErrorMessage.setText(String.format("%s",R.string.no_users_available));
                        textErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void sendFCMTokenToDatabase(String token){
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this,getString(R.string.unable_to_send_token) +
                                        e.getMessage(), Toast.LENGTH_LONG).show());
    }
    private void signOut(){
        Toast.makeText(MainActivity.this,R.string.sign_out,Toast.LENGTH_LONG).show();
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
            Toast.makeText(MainActivity.this,
                    R.string.unable_to_sign_in_out,Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void initiateVideoMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()){
            Toast.makeText(this,
                    user.firstName + " " + user.lastName + getString(R.string.is_not_available_for_meeting),
                    Toast.LENGTH_LONG).show();
        }else {
          Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
          intent.putExtra("user",user);
          intent.putExtra("type","video");
          startActivity(intent);
        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()){
            Toast.makeText(this,
                    user.firstName + " " + user.lastName + R.string.is_not_available_for_meeting,
                    Toast.LENGTH_LONG).show();
        }else {
            Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("type","audio");
            startActivity(intent);
        }
    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected){
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(v -> {
               Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
               intent.putExtra("selectedUsers",new Gson().toJson(usersAdapter.getSelectedUsers()));
               intent.putExtra("type","video");
               intent.putExtra("isMultiple",true);
               startActivity(intent);
            });
        }else {
            imageConference.setVisibility(View.GONE);
        }
    }
}