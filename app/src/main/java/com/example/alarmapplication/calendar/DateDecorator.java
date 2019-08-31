package com.example.alarmapplication.calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.util.Log;

import com.example.alarmapplication.R;
import com.example.alarmapplication.db.DateContract;
import com.example.alarmapplication.db.DbHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDecorator implements DayViewDecorator {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DbHelper dbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;
    private String explain="";


    private final Calendar calendar = Calendar.getInstance();

    public DateDecorator(Context mContext) {
        dbHelper = new DbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        this.mContext = mContext;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Date d = day.getDate();
        String date = dateFormat.format(d);
        Log.d(date,date);
        Cursor c = mDb.rawQuery("select * from "+ DateContract.DateContractEntry.TABLE_NAME+" where "+ DateContract.DateContractEntry.COLUMN_DATE
                +"='"+date+"'",null);
        if(c.getCount()>0) {
            c.moveToFirst();
            explain = c.getString(c.getColumnIndex(DateContract.DateContractEntry.COLUMN_NAME)).substring(0,1); //여기 바꾸기
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CalendarViewSpan(explain+".."));
    }


    public class CalendarViewSpan implements LineBackgroundSpan{
        String text;
        public CalendarViewSpan(String text){
            this.text = text;
        }

        @Override
        public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
            p.setColor(mContext.getResources().getColor(R.color.colorSub4));
            text = this.text;
            c.drawText(String.valueOf(text), (left+right)/3, bottom+20, p );
        }
    }
}
