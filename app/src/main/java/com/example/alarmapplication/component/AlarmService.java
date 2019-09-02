package com.example.alarmapplication.component;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.alarmapplication.R;
import com.example.alarmapplication.activity.DialogActivity;
import com.example.alarmapplication.activity.MainActivity;
import com.example.alarmapplication.db.AlarmContract;
import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.example.alarmapplication.db.DbHelper_alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends JobIntentService {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    static final int JOB_ID = 1001;
    private SQLiteDatabase mDb,nDb;
    private DbHelper dbHelper;
    private DbHelper_alarm dbHelper_alarm;
    private Calendar calendar;
    private Date date;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH시mm분");

    private String recentDate="";
    private String recentTime="";
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AlarmService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        dbHelper_alarm = new DbHelper_alarm(getApplicationContext());
        nDb =dbHelper_alarm.getWritableDatabase();
        dbHelper = new DbHelper(getApplicationContext());
        mDb = dbHelper.getWritableDatabase();
        calendar=Calendar.getInstance();

        //String getState = intent.getExtras().getString("state");
        Log.d("onStartCommand() 실행", "서비스 시작");
        //String input = intent.getStringExtra("inputExtra"); 백그라운드 상태일때 NULL로 뜸 NULL처리를 하거나 없애거나 일단은 IF( EQAUL ALARM ON ) 지움
        /*
        Timer timer = new Timer();
            TimerTask TT = new TimerTask() {
                @Override
                public void run() {
                    // 반복실행할 구문
                    Cursor c = nDb.rawQuery("select * from "+ AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" desc, "
                            +AlarmContract.Entry.COLUMN_TIME+" desc",null);
                    if(c!=null){
                        c.moveToFirst();
                        String date = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_DAY));
                        String alarmTime = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_TIME));

                        init(date,alarmTime);
                    }
                    c.close();
                    notification("큰일났어!", "message", AlarmService.this);
                }

            };
        timer.schedule(TT,0,100000);
        */
        notification("큰일났어!", "message", AlarmService.this);
        date = new Date();
        String str = sdf.format(date);
        String date = str.substring(0,10);
        String time = str.substring(11,17);
        Cursor c = nDb.rawQuery("select * from "+ AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" asc, "
                +AlarmContract.Entry.COLUMN_TIME+" asc",null);
        if(c!=null)
        {
            c.moveToFirst();
            recentTime = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_TIME));
            recentDate = c.getString(c.getColumnIndex((AlarmContract.Entry.COLUMN_DAY)));
            Log.d("Date to String",date+" "+time);
            Log.d("cursor", recentDate+" "+recentTime);
            c.close();
            if(recentTime!=null || recentDate!=null)
                nDb.delete(AlarmContract.Entry.TABLE_NAME,AlarmContract.Entry.COLUMN_DAY+"=?" +" and "+AlarmContract.Entry.COLUMN_TIME+"=?", new String[] {recentDate,recentTime});
            else
                nDb.delete(AlarmContract.Entry.TABLE_NAME,AlarmContract.Entry.COLUMN_DAY+"=?" +" and "+AlarmContract.Entry.COLUMN_TIME+"=?", new String[] {date,time});
        } else{
            nDb.delete(AlarmContract.Entry.TABLE_NAME,AlarmContract.Entry.COLUMN_DAY+"=?" +" and "+AlarmContract.Entry.COLUMN_TIME+"=?", new String[] {date,time});
        }
        stopSelf();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy() 실행", "서비스 파괴");
        final PendingIntent pendingIntent;
        final AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        my_intent.putExtra("state","alarm on");
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, my_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Cursor c = nDb.rawQuery("select * from "+ AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" asc, "
                +AlarmContract.Entry.COLUMN_TIME+" asc",null);
        if(c!=null){
            c.moveToFirst();
            String date = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_DAY));
            String alarmTime = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_TIME));
            init(date,alarmTime);
        }
        c.close();
        Log.d("before reset",calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+
                calendar.get(Calendar.HOUR_OF_DAY)+"시"+calendar.get(Calendar.MINUTE)+"분");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    pendingIntent);
        }
    }


    public void notification(String title, String message, Context context) {
        String text = "";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = createID();
        String channelId = "channel-id";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Cursor c = nDb.rawQuery("select * from "+ AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" asc, "
                +AlarmContract.Entry.COLUMN_TIME+" asc",null);
        if(c!=null) {
            c.moveToFirst();
            int key = c.getInt(c.getColumnIndex(AlarmContract.Entry.COLUMN_KEY));
            Cursor c2 = mDb.rawQuery("select * from "+ DateContract.DateContractEntry.TABLE_NAME+" where "+DateContract.DateContractEntry._ID+"="+key,null);
            if(c2!=null) {
                c2.moveToFirst();
                text = c2.getString(c2.getColumnIndex(DateContract.DateContractEntry.COLUMN_NAME));
            }
            c2.close();
        }
        c.close();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(new long[]{100, 250})
                .setLights(Color.YELLOW, 500, 5000)
                .setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.noti);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            mBuilder.setSmallIcon(R.drawable.noti);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, DialogActivity.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.KOREA).format(now));
        return id;
    }

   private void init(String date, String alarmTime){
       int year = Integer.parseInt(date.substring(0,4));
       int month = Integer.parseInt(date.substring(5,7))-1;
       int day = Integer.parseInt(date.substring(8,10));

       int hour = Integer.parseInt(alarmTime.substring(0,2));
       int minute = Integer.parseInt(alarmTime.substring(3,5));
       calendar.set(year,month,day);
       calendar.set(Calendar.HOUR_OF_DAY, hour);
       calendar.set(Calendar.MINUTE, minute);
   }
}
