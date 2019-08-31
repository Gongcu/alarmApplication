package com.example.alarmapplication;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FireBaseIntanceIDService extends FirebaseInstanceIdService {
    private static String TAG="MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,token);
        sendRegistrationToServer(token);

    }
    private void sendRegistrationToServer(String token){

    }
}
