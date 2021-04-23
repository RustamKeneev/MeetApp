package com.onlineapteka.meetapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.utilities.Constants;

public class IncomingInvitationActivity extends AppCompatActivity {

    private ImageView imageMeetingType;
    private TextView textFirstChar;
    private TextView textUserName;
    private TextView textEmail;

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
}