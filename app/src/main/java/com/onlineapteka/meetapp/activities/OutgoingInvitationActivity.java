package com.onlineapteka.meetapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.models.User;

public class OutgoingInvitationActivity extends AppCompatActivity {
    private ImageView imageMeetingType;
    private TextView textFirstChar;
    private TextView textUserName;
    private TextView textEmail;
    private ImageView imageStopInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);
        initViews();
    }

    private void initViews() {
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
    }
}