package com.example.alarmapplication.db;

import android.provider.BaseColumns;

public class DateContract {
    private String name;
    private String date;
    private String time;
    private int id;

    public DateContract(String name, String date, String time) {
        this.name =name;
        this.date = date;
        this.time = time;
    }

    //BaseColumn 인터페이스는 자동으로 _ID 고유 기본기 생성
    public static final class DateContractEntry implements BaseColumns {
        public static final String TABLE_NAME="dateTable";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_TIME="timesec";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        //public static final String COULUMN_ALRAMM = "alram" 알람 언제 전에
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
