package com.example.alarmapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dateTable.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //실제 DB생성
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_DATE_TABLE="CREATE TABLE IF NOT EXISTS " +
                DateContract.DateContractEntry.TABLE_NAME + " (" +
                DateContract.DateContractEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DateContract.DateContractEntry.COLUMN_NAME + " TEXT NOT NULL, " + //한국 시간
                DateContract.DateContractEntry.COLUMN_DATE + " TEXT NOT NULL, " + //한국 시간
                DateContract.DateContractEntry.COLUMN_TIME + " TEXT NOT NULL, " + //한국 시간
                DateContract.DateContractEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+ //국제표준시
                ");";
        // 쿼리 실행
        sqLiteDatabase.execSQL(SQL_CREATE_DATE_TABLE);
    }
    //DB 스키마가 최근 것을 반영하게함
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 버전이 바뀌면 예전 버전의 테이블을 삭제 (나중에 ALTER 문으로 대체)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DateContract.DateContractEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}