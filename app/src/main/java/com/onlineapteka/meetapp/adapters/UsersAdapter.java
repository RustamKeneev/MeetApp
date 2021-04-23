package com.onlineapteka.meetapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onlineapteka.meetapp.R;
import com.onlineapteka.meetapp.listeners.UserListeners;
import com.onlineapteka.meetapp.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    private List<User> userList;
    private UserListeners userListeners;

    public UsersAdapter(List<User> userList,UserListeners userListeners) {
        this.userList = userList;
        this.userListeners = userListeners;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_container_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.setUserData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

     class UsersViewHolder extends RecyclerView.ViewHolder{
        TextView textFirstChar;
        TextView textUserName;
        TextView textEmail;
        ImageView imageVideoMeeting;
        ImageView imageAudioMeeting;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.text_first_char);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textEmail = itemView.findViewById(R.id.text_email);
            imageVideoMeeting = itemView.findViewById(R.id.image_video_meeting);
            imageAudioMeeting = itemView.findViewById(R.id.image_audio_meeting);
        }
        void setUserData(User user){
            textFirstChar.setText(user.firstName.substring(0,1));
            textUserName.setText(String.format("%s %s",user.firstName,user.lastName));
            textEmail.setText(user.email);
            imageAudioMeeting.setOnClickListener(v ->
                    userListeners.initiateAudioMeeting(user));
            imageVideoMeeting.setOnClickListener(v ->
                    userListeners.initiateVideoMeeting(user));
        }
    }
}
