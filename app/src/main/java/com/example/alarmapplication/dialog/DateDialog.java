package com.example.alarmapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.alarmapplication.listener.DialogListener;
import com.example.alarmapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private DialogListener listener;
    private Button saveBtn;
    private Button quitBtn;
    private String name="";
    private String date="";
    private String time ="";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
    private DatePicker datePicker;
    private boolean edit;
    private static boolean DIRECT_EDIT=false;
    private TimeDialog dialog;
    private TimePicker timePicker;
    Calendar calendar;
    String minute="";
    String hour="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_date_input);

        //다이얼로그 밖의 화면은 흐리게 및 사이즈 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        //기본값으로 오늘 날짜 설정
        Date d =new Date();
        date = dateFormat.format(d);

        calendar = Calendar.getInstance();
        timePicker = findViewById(R.id.timePicker);
        datePicker = findViewById(R.id.datePicker);
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

        //셋팅
        saveBtn=(Button)findViewById(R.id.saveBtn);
        quitBtn=(Button)findViewById(R.id.quitBtn);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        saveBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
    }
    public DateDialog(@NonNull Context context) {
        super(context);
        mContext=context;
        this.edit=false;
    }
    public DateDialog(@NonNull Context context, boolean edit) {
        super(context);
        this.edit=edit;
    }
    public DateDialog(@NonNull Context context, String name) {
        super(context);
        this.name=name;
    }


    public void setDialogListener(DialogListener dialogListener){
        this.listener = dialogListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveBtn:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour() + "";
                    setMinute(timePicker.getMinute());
                } else {
                    hour = timePicker.getCurrentHour() + "";
                    setMinute(timePicker.getCurrentMinute());
                }
                String time = hour +"시 " +minute+"분";
                listener.onPositiveClicked(name,date,time);

                //listener.onPositiveClicked(name,date);
                dismiss();
                break;
            case R.id.quitBtn:
                listener.onNegativeClicked();
                cancel();
        }
    }

    private void setTime(String time){
        this.time = time;
    }

    private void setMinute(int min) {
        if (min >= 10)
            minute = min + "";
        else
            minute = "0" + min;
    }

}
