package com.example.alarmapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.alarmapplication.listener.DialogListener;
import com.example.alarmapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private DialogListener listener;
    private Button saveBtn;
    private Button quitBtn;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
    private TimePicker timePicker;
    private Spinner spinner;
    private CheckBox checkBox;
    private boolean edit;
    private static boolean DIRECT_EDIT=false;
    private ArrayAdapter<String> arrayAdapter;
    Calendar calendar;
    String minute="";
    String hour="";
    String name="";
    String date="";
    String time="";
    private int alarmDay;
    private String alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_input);

        //다이얼로그 밖의 화면은 흐리게 및 사이즈 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        spinner = findViewById(R.id.spinner);
        ArrayList arrayList = new ArrayList<>();
        arrayList.add("당일");
        arrayList.add("하루 전");
        arrayList.add("3일 전");
        arrayList.add("7일 전");
        arrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item,arrayList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        alarmDay=0;
                        break;
                    case 1:
                        alarmDay=1;
                        break;
                    case 2:
                        alarmDay=3;
                        break;
                    case 3:
                        alarmDay=7;
                        break;
            }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        timePicker = findViewById(R.id.timePicker);

        //셋팅
        saveBtn=(Button)findViewById(R.id.saveBtn);
        quitBtn=(Button)findViewById(R.id.quitBtn);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        saveBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);

    }
    public TimeDialog(@NonNull Context context) {
        super(context);
        mContext=context;
        this.edit=false;
    }
    public TimeDialog(@NonNull Context context, boolean edit) {
        super(context);
        this.edit=edit;
    }
    public TimeDialog(@NonNull Context context, String name,String date,String time) {
        super(context);
        mContext=context;
        this.name=name;
        this.date=date;
        this.time=time;

    }
    private void setMinute(int min) {
        if (min >= 10)
            minute = min + "";
        else
            minute = "0" + min;
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
                String alarmTime = hour +"시 " +minute+"분";
                try {
                    listener.onPositiveClicked(name, date, time, alarmDay, alarmTime);
                }catch (NullPointerException e){e.printStackTrace(); Toast.makeText(mContext,"알람을 받을 날을 선택해주세요.",Toast.LENGTH_LONG).show();}
                dismiss();
                break;
            case R.id.quitBtn:
                listener.onNegativeClicked();
                cancel();
        }
    }


}
