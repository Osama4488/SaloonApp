package com.example.saloonapp.Models;

public class ParlourTimingsModel {
    private String parlourTimingId, parlourTimingDay, parlourTimingOpenTime, parlourTimingCloseTime;

    public ParlourTimingsModel(String parlourTimingId, String parlourTimingDay, String parlourTimingOpenTime, String parlourTimingCloseTime) {
        this.parlourTimingId = parlourTimingId;
        this.parlourTimingDay = parlourTimingDay;
        this.parlourTimingOpenTime = parlourTimingOpenTime;
        this.parlourTimingCloseTime = parlourTimingCloseTime;
    }

    public String getParlourTimingId() {
        return parlourTimingId;
    }

    public String getParlourTimingDay() {
        return parlourTimingDay;
    }

    public String getParlourTimingOpenTime() {
        return parlourTimingOpenTime;
    }

    public String getParlourTimingCloseTime() {
        return parlourTimingCloseTime;
    }
}
