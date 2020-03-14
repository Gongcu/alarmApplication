package com.example.alarmapplication.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alarmapplication.activity.DialogActivity;
import com.example.alarmapplication.calendar.DateDecorator;
import com.example.alarmapplication.db.DbHelper_alarm;
import com.example.alarmapplication.db.EntireContract;
import com.example.alarmapplication.dialog.DataDialog;
import com.example.alarmapplication.R;
import com.example.alarmapplication.adapter.RecyclerAdapter;
import com.example.alarmapplication.calendar.OneDayDecorator;
import com.example.alarmapplication.calendar.SaturdayDecorator;
import com.example.alarmapplication.calendar.SundayDecorator;
import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.example.alarmapplication.dialog.DateDialog;
import com.example.alarmapplication.dialog.TimeDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    private DbHelper dbHelper;
    private DbHelper_alarm dbHelper_alarm;
    private SQLiteDatabase mDb,nDb;

    private MaterialCalendarView materialCalendarView;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
    private ArrayList<EntireContract> list;
    private static final ArrayList<EntireContract> EMPTY_LIST=new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private Button button;
    private DataDialog dialog;
    private DateDialog dialog_date;
    private TimeDialog dialog_time;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new DbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();
        dbHelper_alarm = new DbHelper_alarm(getActivity());
        nDb = dbHelper_alarm.getWritableDatabase();

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        materialCalendarView = view.findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator(),
                new DateDecorator(getActivity()));

        //Initialize
        Date d = new Date();
        String strDate =sdf.format(d);
        setRecyclerView(strDate);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Date d = date.getDate();
                String strDate =sdf.format(d);
                String text=sdfDay.format(d);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);

                setRecyclerView(strDate);
            }
        });

        button = view.findViewById(R.id.imageAddButton);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(getActivity(), DialogActivity.class);
                startActivity(intent);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


    public void setRecyclerView(String date){
        long id=0;
        Cursor c = mDb.rawQuery("select * from dateTable where "+ DateContract.DateContractEntry.COLUMN_DATE+"='"+date+"'",null);
        if(c.getCount()==0) {
            adapter = new RecyclerAdapter(getActivity(),EMPTY_LIST);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            c.close();
            return;
        }else {
            c.moveToFirst();
            list = new ArrayList<>();
            do {
                String name = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_NAME));
                String strDate = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_DATE));
                String time = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_TIME));
                int current_id = c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID));
                EntireContract data = new EntireContract(name, strDate, time);
                data.setId(current_id);
                list.add(data);
            } while (c.moveToNext());
            c.close();
            adapter = new RecyclerAdapter(getActivity(), list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(adapter);
        }
    }

    public String dayToString(int dayNum){
        String day=null;
        switch(dayNum){
            case 1:
                day = "Sun";
                break ;
            case 2:
                day = "Mon";
                break ;
            case 3:
                day = "Tue";
                break ;
            case 4:
                day = "Wed";
                break ;
            case 5:
                day = "Thu";
                break ;
            case 6:
                day = "Fri";
                break ;
            case 7:
                day = "Sat";
                break ;

        }
        return day;
    }

    public void update(){
        Date date = new Date();
        String strDate = sdf.format(date);
        setRecyclerView(strDate);
    }
}
