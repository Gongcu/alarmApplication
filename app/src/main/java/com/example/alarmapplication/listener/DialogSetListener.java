package com.example.alarmapplication.listener;

public interface DialogSetListener {
    public void onDateDialogClicked(String date);
    public void onDataDialogClicked(String date, String time);
    public void onPositiveClicked(String name, String date, String time);
    public void onNegativeClicked();
}
