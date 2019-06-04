package com.example.saloonapp.Models;

public class ScheduleModel {
    private String dayName;
    private String openTime;
    private String closeTime;

    public ScheduleModel(String dayName, String openTime, String closeTime) {
        this.dayName = dayName;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public String getDayName() {
        return dayName;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }
}
