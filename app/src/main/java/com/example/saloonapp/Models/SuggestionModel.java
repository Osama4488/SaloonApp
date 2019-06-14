package com.example.saloonapp.Models;

public class SuggestionModel {
    private String parlourId, parlourName, parlourBookings, parlourExpert, parlourRating, lat, lng;

    public SuggestionModel(String parlourId, String parlourName, String parlourBookings, String parlourExpert, String parlourRating, String lat, String lng) {
        this.parlourId = parlourId;
        this.parlourName = parlourName;
        this.parlourBookings = parlourBookings;
        this.parlourExpert = parlourExpert;
        this.parlourRating = parlourRating;
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getParlourId() {
        return parlourId;
    }

    public String getParlourName() {
        return parlourName;
    }

    public String getParlourBookings() {
        return parlourBookings;
    }

    public String getParlourExpert() {
        return parlourExpert;
    }

    public String getParlourRating() {
        return parlourRating;
    }
}
