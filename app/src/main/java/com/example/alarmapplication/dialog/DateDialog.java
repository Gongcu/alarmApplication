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

import com.example.alarmapplication.calendar.DateDecorator;
import com.example.alarmapplication.calendar.OneDayDecorator;
import com.example.alarmapplication.calendar.SaturdayDecorator;
import com.example.alarmapplication.calendar.SundayDecorator;
import com.example.alarmapplication.listener.DialogListener;
import com.example.alarmapplication.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDialog extends Dialog {
    private Context mContext;
    private DialogListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private MaterialCalendarView calendarView;
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

        calendarView = findViewById(R.id.calendarView);
        //기본값으로 오늘 날짜 설정
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)                .setMinimumDate(CalendarDay.from(2019, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator(),
                new DateDecorator(mContext));

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Date d = date.getDate();
                String strDate =sdf.format(d);
                listener.onPositiveClicked(strDate);
                dismiss();
            }
        });

    }

    public DateDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }


    public void setDialogListener(DialogListener dialogListener){
        this.listener = dialogListener;
    }



    private void setMinute(int min) {
        if (min >= 10)
            minute = min + "";
        else
            minute = "0" + min;
    }

}
