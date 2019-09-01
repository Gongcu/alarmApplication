package com.example.alarmapplication.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    public static final String TAG = "AlarmReceiver.class";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // intent로부터 전달받은 string
        String get_your_string = intent.getExtras().getString("state");

        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, AlarmService.class);
        // RingtonePlayinService로 extra string값 보내기
        //service_intent.putExtra("state", get_your_string);
        /*
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            try {
                Log.d(TAG,"BOOT COMPLETE");
                service_intent.setAction("android.intent.action.BOOT_COMPLETED");
                AlarmService.enqueueWork(context,service_intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(get_your_string.equals("alarm on"))
            AlarmService.enqueueWork(context,service_intent);
*/
        AlarmService.enqueueWork(context,service_intent);
        //AlarmService.enqueueWork(context,service_intent);
        // start the ringtone service
/*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.enque(service_intent);
        }else{
            this.context.startService(service_intent);
        }*/
    }

}
