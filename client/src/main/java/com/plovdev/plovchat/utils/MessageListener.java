package com.plovdev.plovchat.utils;

import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;

public interface MessageListener {
    void onMessageReceived(Message message);
    void onAuntithicated();
    void onStatusChanged(boolean status, User user);
}