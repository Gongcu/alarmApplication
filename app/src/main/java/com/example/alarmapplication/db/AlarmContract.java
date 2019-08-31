package com.example.alarmapplication.db;

import android.provider.BaseColumns;

public class AlarmContract {
    private int day;
    private String time;
    private int id;

    public AlarmContract(int day, String time) {
        this.day =day;
        this.time = time;
    }

    //BaseColumn 인터페이스는 자동으로 _ID 고유 기본기 생성
    public static final class Entry implements BaseColumns {
        public static final String TABLE_NAME="alarmTable";
        public static final String COLUMN_DAY="day";
        public static final String COLUMN_TIME="time";
        public static final String COLUMN_KEY="parent_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }


}
