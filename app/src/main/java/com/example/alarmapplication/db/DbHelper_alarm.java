package com.example.alarmapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper_alarm extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarmTable.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper_alarm(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //실제 DB생성
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_ALARM_TABLE="CREATE TABLE IF NOT EXISTS " +
                AlarmContract.Entry.TABLE_NAME + " (" +
                AlarmContract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlarmContract.Entry.COLUMN_DAY + " TEXT NOT NULL, " + //한국 시간
                AlarmContract.Entry.COLUMN_TIME + " TEXT NOT NULL, " + //한국 시간
                AlarmContract.Entry.COLUMN_KEY + " INTEGER NOT NULL, " + //한국 시간
                AlarmContract.Entry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                " FOREIGN KEY ("+AlarmContract.Entry.COLUMN_KEY+") REFERENCES "+ DateContract.DateContractEntry.TABLE_NAME +" ("+DateContract.DateContractEntry._ID+"));";
        // 쿼리 실행

        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");
        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);
    }
    //DB 스키마가 최근 것을 반영하게함
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 버전이 바뀌면 예전 버전의 테이블을 삭제 (나중에 ALTER 문으로 대체)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AlarmContract.Entry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}