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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.alarmapplication.R;
import com.example.alarmapplication.adapter.RecyclerAdapter;
import com.example.alarmapplication.adapter.RecyclerAdapter_today;
import com.example.alarmapplication.db.AlarmContract;
import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.example.alarmapplication.db.DbHelper_alarm;
import com.example.alarmapplication.db.EntireContract;
import com.example.alarmapplication.dialog.DateDialog;
import com.example.alarmapplication.listener.DialogListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment{
    private static final String TAG ="DataFragment";
    private DbHelper dbHelper;
    private DbHelper_alarm dbHelper_alarm;
    private SQLiteDatabase mDb, nDb;
    private RecyclerAdapter_today adapter;

    private RecyclerView recyclerView;
    private ArrayList<EntireContract> list;
    private static final ArrayList<EntireContract> EMPTY_LIST=new ArrayList<>();


    public DataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHelper = new DbHelper(getActivity());
        dbHelper_alarm = new DbHelper_alarm(getActivity());
        mDb = dbHelper.getWritableDatabase();
        nDb = dbHelper_alarm.getWritableDatabase();

        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        setRecyclerView();
        super.onViewCreated(view, savedInstanceState);
    }



    public void setRecyclerView(){
        long id=0;
        Cursor c = mDb.rawQuery("select * from dateTable"+" order by "+
                DateContract.DateContractEntry.COLUMN_DATE+" asc, "+DateContract.DateContractEntry.COLUMN_TIME+" asc",null);
        if(c.getCount()==0) {
            adapter = new RecyclerAdapter_today(getActivity(),EMPTY_LIST);
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
                String alarm="";
                int current_id = c.getInt(c.getColumnIndex(DateContract.DateContractEntry._ID));
                Cursor cursor = nDb.rawQuery("select * from "+ AlarmContract.Entry.TABLE_NAME+" where "+AlarmContract.Entry.COLUMN_KEY+"="+current_id,null);
                //알람 있을 경우
                if(cursor.getCount()>0){
                    cursor.moveToFirst();
                    alarm = cursor.getString(cursor.getColumnIndex(AlarmContract.Entry.COLUMN_DAY))+" "
                            +cursor.getString(cursor.getColumnIndex(AlarmContract.Entry.COLUMN_TIME));
                    EntireContract data = new EntireContract(name,strDate,time,alarm, current_id);
                    list.add(data);
                }
                //알람 없을 경우
                else{
                    EntireContract data = new EntireContract(name,strDate,time,"알람 없음", current_id);
                    list.add(data);
                }
                cursor.close();
            } while (c.moveToNext());
            c.close();
            adapter = new RecyclerAdapter_today(getActivity(), list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(adapter);
        }
    }

}
