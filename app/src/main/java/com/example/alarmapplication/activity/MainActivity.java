package com.example.alarmapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.alarmapplication.ContentsPagerAdapter;
import com.example.alarmapplication.R;
import com.example.alarmapplication.db.DbHelper_alarm;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AlarmManager alarm_manager;

    private ContentsPagerAdapter mContentsPagerAdapter;

    private DbHelper_alarm dbHelper_alarm;
    private SQLiteDatabase nDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        dbHelper_alarm = new DbHelper_alarm(getApplicationContext());
        nDb = dbHelper_alarm.getWritableDatabase();


        mViewPager = (ViewPager) findViewById(R.id.pager_content);
        mTabLayout = (TabLayout) findViewById(R.id.layout_tab);
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("캘린더")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("할 일")));

        mContentsPagerAdapter = new ContentsPagerAdapter(
                getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(mContentsPagerAdapter);

        mViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
                }
        );
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mContentsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i){}
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });
    }

    private View createTabView(String tabName) {

        View tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.txt_name);
        txt_name.setText(tabName);
        return tabView;

    }

    public class AlarmHATT {
        private Context context;
        public AlarmHATT(Context context) {
            this.context=context;
        }
        public void Alarm() {
            //AlarmManager는 device에 미래에 대한 알림같은 행위를 등록할 때 사용합니다.
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            //알람이 발생했을 경우, BradcastD에게 방송을 해주기 위해서 명시적으로 알려줍니다.
            Intent intent = new Intent("com.example.alarmtestous.ALARM_START");
            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            /*
            /*다중 order 이중에 알람이 없는거 체크 해야되는 상태
            Cursor cursor = nDb.rawQuery("select * from "+AlarmContract.Entry.TABLE_NAME+" order by "+AlarmContract.Entry.COLUMN_DAY+" desc, "+
                    AlarmContract.Entry.COLUMN_TIME+" desc", null);
            if(cursor!=null){
                for(int i=0; i<cursor.getCount(); i++){
                    cursor.moveToNext();
                    //Log.d("day",c.getString())
                }
                cursor.close();
            }*/

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
                    0,
                    48,
                    0);

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
}
