package com.example.saloonapp.Models;

import java.io.Serializable;

public class ParlourModel implements Serializable {
    private Integer parlourId;
    private String parlourName;
    private Double parlourRating;
    private String parlourNumber;

    public ParlourModel(Integer parlourId, String parlourName, Double parlourRating, String parlourNumber) {
        this.parlourId = parlourId;
        this.parlourName = parlourName;
        this.parlourRating = parlourRating;
        this.parlourNumber = parlourNumber;
    }

    public String getParlourNumber() {
        return parlourNumber;
    }

    public Integer getParlourId() {
        return parlourId;
    }

    public String getParlourName() {
        return parlourName;
    }

    public Double getParlourRating() {
        return parlourRating;
    }
}
