package com.example.alarmapplication.listener;

public interface DialogListener {
    public void onPositiveClicked(String date);
    public void onPositiveClicked(String date, String time);
    public void onPositiveClicked(String name, String date, String time);
    public void onPositiveClicked(String name, String date, String time, int alarmDay, String alarmTime);
    public void onNegativeClicked();
}
