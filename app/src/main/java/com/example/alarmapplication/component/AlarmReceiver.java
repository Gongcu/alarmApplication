package com.example.alarmapplication.component;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.alarmapplication.db.DbHelper_alarm;

public class AlarmReceiver extends BroadcastReceiver {
    private static Context context;
    private static final String ALARM="alarm on";
    public static final String TAG = "AlarmReceiver.class";
    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String get_your_string = intent.getExtras().getString("state");
        if(get_your_string.equals(ALARM)) {
            Intent service_intent = new Intent(context, AlarmService.class);
            AlarmService.enqueueWork(context, service_intent);
        }
    }
}
