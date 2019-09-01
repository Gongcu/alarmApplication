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

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                        // TODO Auto-generated method stub

                        if(monthOfYear<9) {
                            if (dayOfMonth < 10)
                                date = year + "-0" + (monthOfYear+1) + "-0" + dayOfMonth;
                            else
                                date = year + "-0" + (monthOfYear+1)  + "-" + dayOfMonth;
                        }
                        else {
                            if (dayOfMonth < 10)
                                date = year + "-" + (monthOfYear+1)  + "-0" + dayOfMonth;
                            else
                                date = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                        }
                    }
                });

        aDatePicker.init(aDatePicker.getYear(), aDatePicker.getMonth(), aDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                        //calendar.set(Calendar.YEAR,year);
                        //calendar.set(Calendar.MONTH,monthOfYear);
                        //calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        // TODO Auto-generated method stub
                        if(monthOfYear<9) {
                            if (dayOfMonth < 10)
                                alarmDate = year + "-0" + (monthOfYear+1) + "-0" + dayOfMonth;
                            else
                                alarmDate = year + "-0" + (monthOfYear+1)  + "-" + dayOfMonth;
                        }
                        else {
                            if (dayOfMonth < 10)
                                alarmDate = year + "-" + (monthOfYear+1)  + "-0" + dayOfMonth;
                            else
                                alarmDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                        }
                    }
                });


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
                        hour = timePicker.getHour() + "";
                        setMinute(timePicker.getMinute());
                    } else {
                        hour = timePicker.getCurrentHour() + "";
                        setMinute(timePicker.getCurrentMinute());
                    }
                    String time = hour +"시 " +minute+"분";
                    if(checkBox.isChecked()){
                        addData(name,date,time);
                    }else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = timePicker.getHour() + "";
                            setMinute(timePicker.getMinute());
                            calendar.set(Calendar.HOUR_OF_DAY,aTimePicker.getHour());
                            calendar.set(Calendar.MINUTE,aTimePicker.getMinute());
                        } else {
                            hour = timePicker.getCurrentHour() + "";
                            setMinute(timePicker.getCurrentMinute());
                            calendar.set(Calendar.HOUR_OF_DAY,aTimePicker.getCurrentHour());
                            calendar.set(Calendar.MINUTE,aTimePicker.getCurrentMinute());

                        }
                        String alarmTime = hour +"시 " +minute+"분";
                        addAlarm(alarmDate, alarmTime, addData(name, date, time));
                        Toast.makeText(DialogActivity.this,"Alarm 예정 " + calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.MONTH)+" "+calendar.get(Calendar.DAY_OF_MONTH)+" "
                                + alarmTime,Toast.LENGTH_SHORT).show();
                        final Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);

                        my_intent.putExtra("state","alarm on");
                        pendingIntent = PendingIntent.getBroadcast(DialogActivity.this, 0, my_intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                pendingIntent);
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
            c.moveToFirst();
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

    private void setMinute(int min) {
        if (min >= 10)
            minute = min + "";
        else
            minute = "0" + min;
    }
}
