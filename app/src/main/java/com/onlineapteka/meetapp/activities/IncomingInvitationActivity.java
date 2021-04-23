package com.onlineapteka.meetapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.network.ApiClient;
import com.onlineapteka.meetapp.network.ApiService;
import com.onlineapteka.meetapp.utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitationActivity extends AppCompatActivity {

    private ImageView imageMeetingType;
    private TextView textFirstChar;
    private TextView textUserName;
    private TextView textEmail;
    private ImageView imageAcceptInvitation;
    private ImageView imageRejectInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);
        initViews();
    }

    private void initViews() {
        imageMeetingType = findViewById(R.id.image_meeting_type);
        textFirstChar = findViewById(R.id.text_first_char);
        textUserName = findViewById(R.id.text_user_name);
        textEmail = findViewById(R.id.text_email);
        imageAcceptInvitation = findViewById(R.id.image_accept_invitation);
        imageRejectInvitation = findViewById(R.id.image_reject_invitation);

        imageAcceptInvitation.setOnClickListener(v -> {
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });

        imageRejectInvitation.setOnClickListener(v -> {
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });


        String meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        if (meetingType !=null){
            if (meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }

        String firstName = getIntent().getStringExtra(Constants.KEY_FIRST_NAME);
        if (firstName !=null){
            textFirstChar.setText(firstName.substring(0,1));
        }
        textUserName.setText(String.format("%s %s",firstName,getIntent().getStringExtra(Constants.KEY_LAST_NAME)));
        textEmail.setText(getIntent().getStringExtra(Constants.KEY_EMAIL));
    }


    private void sendInvitationResponse(String type,String receiverToken){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            JSONObject body = new  JSONObject();
            JSONObject data = new  JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type);
            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);
            sendRemoteMessage(body.toString(),type);
        }catch (Exception e){
            Toast.makeText(IncomingInvitationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody,String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                        try {
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverURL)
                                    .setWelcomePageEnabled(false)
                                    .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
//                                    .setAudioMuted(false)
//                                    .setVideoMuted(false)
//                                    .setAudioOnly(false)
                                    .build();
                            JitsiMeetActivity.launch(IncomingInvitationActivity.this,options);
                            finish();
                        }catch (Exception e){
                            Toast.makeText(IncomingInvitationActivity.this,e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }else {
                        Toast.makeText(IncomingInvitationActivity.this,"Invitation Rejected",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else {
                    Toast.makeText(IncomingInvitationActivity.this,
                            response.message(),Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(IncomingInvitationActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type !=null){
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)){
                    Toast.makeText(context,"Invitation Canceled ",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver);
    }
}