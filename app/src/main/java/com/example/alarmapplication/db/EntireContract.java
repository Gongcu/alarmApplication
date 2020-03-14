package com.example.alarmapplication.db;

public class EntireContract {
    private String name;
    private String date;
    private String time;
    private String alarm;
    private int id;

    public EntireContract(String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }


    public EntireContract(String name, String date, String time, String alarm) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
    }

    public EntireContract(String name, String date, String time, String alarm, int id) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
