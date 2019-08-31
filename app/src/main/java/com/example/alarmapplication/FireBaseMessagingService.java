package com.example.alarmapplication;

import com.google.firebase.messaging.RemoteMessage;

public class FireBaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG="FirebaseMsgService";

    private String msg,title;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title = remoteMessage.getNotification().getTitle();
        msg = remoteMessage.getNotification().getBody();
    }
}
