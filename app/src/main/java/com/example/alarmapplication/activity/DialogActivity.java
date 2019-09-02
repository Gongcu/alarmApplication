package com.example.alarmapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alarmapplication.R;
import com.example.alarmapplication.component.AlarmReceiver;
import com.example.alarmapplication.db.AlarmContract;
import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.example.alarmapplication.db.DbHelper_alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editText;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private DatePicker aDatePicker;
    private TimePicker aTimePicker;

    private Button saveBtn, quitBtn;
    private Button keyBoardButton;

    private CheckBox checkBox;

    private LinearLayout alarmLayout;

    private SQLiteDatabase mDb,nDb;
    private DbHelper dbHelper;
    private DbHelper_alarm dbHelper_alarm;

    private String name;
    private String date;
    private String alarmDate;
    private String alarmTime;

    private String deadLineTime;
    private String hour,minute;
    InputMethodManager imm;
    private Calendar calendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        editText =findViewById(R.id.todoEditText);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        aDatePicker = findViewById(R.id.datePicker2);
        aTimePicker = findViewById(R.id.timePicker2);

        saveBtn = findViewById(R.id.saveBtn);
        quitBtn = findViewById(R.id.quitBtn);
        keyBoardButton = findViewById(R.id.keyboardButton);

        checkBox = findViewById(R.id.alarmBox);

        alarmLayout = findViewById(R.id.linearItem);

        initDB();

        Date d =new Date();
        date = dateFormat.format(d);

        saveBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        keyBoardButton.setOnClickListener(this);
        checkBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveBtn:
                if(editText.getText().toString().equals("")){
                    Toast.makeText(DialogActivity.this,"일정을 입력해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    name = editText.getText().toString();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        deadLineTime = timePicker.getHour() + "시";
                        deadLineTime+=setMinute(timePicker.getMinute());
                    } else {
                        deadLineTime = timePicker.getCurrentHour() + "시";
                        deadLineTime+=setMinute(timePicker.getCurrentMinute());
                    }
                    if(checkBox.isChecked()){
                        initDate();
                        addData(name,date,deadLineTime);
                    }else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            deadLineTime = timePicker.getHour() + "시";
                            deadLineTime+=setMinute(timePicker.getMinute());
                            alarmTime= aTimePicker.getHour() + "시";
                            alarmTime+=setMinute(aTimePicker.getMinute());
                        } else {
                            deadLineTime = timePicker.getCurrentHour() + "시";
                            deadLineTime+=setMinute(timePicker.getCurrentMinute());
                            alarmTime= aTimePicker.getCurrentHour() + "시";
                            alarmTime+=setMinute(aTimePicker.getCurrentMinute());
                        }
                        initDate();
                        initAlarmDate();
                        addAlarm(alarmDate, alarmTime, addData(name, date, deadLineTime));

                        Toast.makeText(DialogActivity.this,"Alarm 예정 " + alarmDate+" "+ alarmTime,Toast.LENGTH_SHORT).show();
                        //nDb.delete(AlarmContract.Entry.TABLE_NAME, AlarmContract.Entry.COLUMN_KEY+"=26",null);
                       // nDb.delete(AlarmContract.Entry.TABLE_NAME, AlarmContract.Entry.COLUMN_KEY+"=19",null);
                       // nDb.delete(AlarmContract.Entry.TABLE_NAME, AlarmContract.Entry.COLUMN_KEY+"=21",null);
                        Cursor c = nDb.rawQuery("select * from "+AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" asc, "
                                +AlarmContract.Entry.COLUMN_TIME+" asc",null);
                        if(c!=null){
                            c.moveToFirst();
                            String date = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_DAY));
                            String alarmTime = c.getString(c.getColumnIndex(AlarmContract.Entry.COLUMN_TIME));

                            int year = Integer.parseInt(date.substring(0,4));
                            int month = Integer.parseInt(date.substring(5,7))-1;
                            int day = Integer.parseInt(date.substring(8,10));

                            int hour = Integer.parseInt(alarmTime.substring(0,2));
                            int minute = Integer.parseInt(alarmTime.substring(3,5));
                            calendar.set(year,month,day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND,0);
                        }
                        c.close();
                        final Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        my_intent.putExtra("state","alarm on");
                        pendingIntent = PendingIntent.getBroadcast(DialogActivity.this, 0, my_intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.d("calendar", "날짜 " +calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.MONTH)+" "+calendar.get(Calendar.DAY_OF_MONTH)
                        +" 시간 "+calendar.get(Calendar.HOUR_OF_DAY)+" "+calendar.get(Calendar.MINUTE)+" "+calendar.get(Calendar.SECOND));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    pendingIntent);
                        }
                    }
                }
                Intent intent = new Intent(DialogActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.quitBtn:
                Intent intent2 = new Intent(DialogActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.alarmBox:
                if(checkBox.isChecked()){
                    alarmLayout.setVisibility(View.GONE);
                }else
                    alarmLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.keyboardButton:
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                break;
        }
    }

    public void initDB(){
        dbHelper = new DbHelper(DialogActivity.this);
        dbHelper_alarm = new DbHelper_alarm(DialogActivity.this);
        mDb = dbHelper.getWritableDatabase();
        nDb = dbHelper_alarm.getWritableDatabase();
    }

    public int addData(String name, String date, String time){
        ContentValues cv = new ContentValues();
        cv.put(DateContract.DateContractEntry.COLUMN_NAME,name);
        cv.put(DateContract.DateContractEntry.COLUMN_DATE,date);
        cv.put(DateContract.DateContractEntry.COLUMN_TIME,time);
        mDb.insert(DateContract.DateContractEntry.TABLE_NAME,null,cv);

        Cursor c = mDb.rawQuery("select * from "+ DateContract.DateContractEntry.TABLE_NAME,null);
        if(c.getCount()>0){
            c.moveToLast();
            int id = c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID));
            c.close();
            return  id;
        }else{
            return 0;
        }
    }

    public boolean addAlarm(String date, String time, int PARENT_ID){
        ContentValues cv = new ContentValues();
        cv.put(AlarmContract.Entry.COLUMN_DAY,date);
        cv.put(AlarmContract.Entry.COLUMN_TIME,time);
        cv.put(AlarmContract.Entry.COLUMN_KEY,PARENT_ID);
        return nDb.insert(AlarmContract.Entry.TABLE_NAME,null,cv)>0;
    }

    private String setMinute(int min) {
        if (min >= 10)
            return minute = min + "분";
        else
            return minute = "0" + min+"분";
    }

    private void initDate(){
        if(datePicker.getMonth()<9) {
            if (datePicker.getDayOfMonth() < 10)
                date = datePicker.getYear() + "-0" + (datePicker.getMonth()+1) + "-0" + datePicker.getDayOfMonth();
            else
                date = datePicker.getYear() + "-0" + (datePicker.getMonth()+1)  + "-" + datePicker.getDayOfMonth();
        }
        else {
            if (datePicker.getDayOfMonth()  < 10)
                date = datePicker.getYear() + "-" + (datePicker.getMonth()+1)  + "-0" + datePicker.getDayOfMonth();
            else
                date = datePicker.getYear() + "-" + (datePicker.getMonth()+1) + "-" + datePicker.getDayOfMonth();
        }
    }

    private void initAlarmDate(){
        if(aDatePicker.getMonth()<9) {
            if (aDatePicker.getDayOfMonth() < 10)
                alarmDate = aDatePicker.getYear() + "-0" + (aDatePicker.getMonth()+1) + "-0" + aDatePicker.getDayOfMonth();
            else
                alarmDate = aDatePicker.getYear() + "-0" + (aDatePicker.getMonth()+1)  + "-" + aDatePicker.getDayOfMonth();
        }
        else {
            if (datePicker.getDayOfMonth()  < 10)
                alarmDate = aDatePicker.getYear() + "-" + (aDatePicker.getMonth()+1)  + "-0" + aDatePicker.getDayOfMonth();
            else
                alarmDate = aDatePicker.getYear() + "-" + (aDatePicker.getMonth()+1) + "-" + aDatePicker.getDayOfMonth();
        }
    }
}
