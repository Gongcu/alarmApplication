package com.example.alarmapplication;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.alarmapplication.fragment.CalendarFragment;
import com.example.alarmapplication.fragment.DataFragment;

public class ContentsPagerAdapter extends FragmentStatePagerAdapter {

    private int mPageCount;

    public ContentsPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.mPageCount = pageCount;

    }



    @Override

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CalendarFragment calendarFragment = new CalendarFragment();
                notifyDataSetChanged();
                return calendarFragment;

            case 1:
                DataFragment dataFragment = new DataFragment();
                notifyDataSetChanged();
                return dataFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }


    @Override
    public int getItemPosition(Object object) {
        if (object instanceof CalendarFragment) {
            // Create a new method notifyUpdate() in your fragment
            // it will get call when you invoke
            // notifyDatasetChaged();
            //((CalendarFragment) object).changeDataSet();
        } else if((object instanceof DataFragment)){
            //((DataFragment) object).update();
        }
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }
}