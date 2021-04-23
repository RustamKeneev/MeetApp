package com.onlineapteka.meetapp.activities;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.models.User;
import com.onlineapteka.meetapp.network.ApiClient;
import com.onlineapteka.meetapp.network.ApiService;
import com.onlineapteka.meetapp.utilities.Constants;
import com.onlineapteka.meetapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {
    private ImageView imageMeetingType;
    private TextView textFirstChar;
    private TextView textUserName;
    private TextView textEmail;
    private ImageView imageStopInvitation;

    private PreferenceManager preferenceManager;
    private String inviterToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);
        initViews();
    }

    private void initViews() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() !=null){
                inviterToken = task.getResult().getToken();
            }
        });

        imageMeetingType = findViewById(R.id.image_meeting_type);
        String meetingType = getIntent().getStringExtra("type");
        if (meetingType !=null){
            if (meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }
        textFirstChar = findViewById(R.id.text_first_char);
        textUserName = findViewById(R.id.text_user_name);
        textEmail = findViewById(R.id.text_email);
        imageStopInvitation = findViewById(R.id.image_stop_invitation);

        User user = (User) getIntent().getSerializableExtra("user");
        if (user !=null){
            textFirstChar.setText(user.firstName.substring(0,1));
            textUserName.setText(String.format("%s %s",user.firstName,user.lastName));
            textEmail.setText(user.email);
        }

        imageStopInvitation.setOnClickListener(v -> {
            onBackPressed();
        });

        if (meetingType !=null && user !=null){
            initiateMeeting(meetingType, user.token);
        }
    }

    private void initiateMeeting(String meetingType, String receiverToken){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);
            data.put(Constants.KEY_FIRST_NAME,preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME,preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL,preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN,inviterToken);

            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION);

        }catch (Exception e){
            Toast.makeText(OutgoingInvitationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody,String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this,
                                "Invitation sent successfully", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(OutgoingInvitationActivity.this,
                            response.message(),Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}