package com.onlineapteka.meetapp.listeners;

import com.onlineapteka.meetapp.models.User;

public interface UserListeners {
    void initiateVideoMeeting(User user);
    void initiateAudioMeeting(User user);
    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
