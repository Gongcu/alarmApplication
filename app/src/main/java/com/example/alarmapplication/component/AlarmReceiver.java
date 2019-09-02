package com.example.alarmapplication.component;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static Context context;
    public static final String TAG = "AlarmReceiver.class";
    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String get_your_string = intent.getExtras().getString("state");

        Intent service_intent = new Intent(context, AlarmService.class);
        AlarmService.enqueueWork(context,service_intent);

        //AlarmService.enqueueWork(context,service_intent);
/*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.enque(service_intent);
        }else{
            this.context.startService(service_intent);
        }*/
    }




    private static PendingIntent getPendingIntent() {
        /* get the application context */
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
