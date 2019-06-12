package com.example.saloonapp.Models;

import java.io.Serializable;

public class ParlourModel implements Serializable {
    private String parlourId;
    private String parlourName;
    private Double parlourRating;
    private String parlourNumber;
    private Double parlourLat;
    private Double parlourLng;

    public ParlourModel(String parlourId, String parlourName, Double parlourRating, String parlourNumber, Double parlourLat, Double parlourLng) {
        this.parlourId = parlourId;
        this.parlourName = parlourName;
        this.parlourRating = parlourRating;
        this.parlourNumber = parlourNumber;
        this.parlourLat = parlourLat;
        this.parlourLng = parlourLng;
    }

    public Double getParlourLat() {
        return parlourLat;
    }

    public Double getParlourLng() {
        return parlourLng;
    }

    public String getParlourNumber() {
        return parlourNumber;
    }

    public String getParlourId() {
        return parlourId;
    }

    public String getParlourName() {
        return parlourName;
    }

    public Double getParlourRating() {
        return parlourRating;
    }
}
