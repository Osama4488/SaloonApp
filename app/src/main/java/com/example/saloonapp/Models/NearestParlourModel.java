package com.example.saloonapp.Models;

import java.io.Serializable;

public class NearestParlourModel {
    private ParlourModel parlourDetails;
    private Double parlourDistance;

    public NearestParlourModel(ParlourModel parlourDetails, Double parlourDistance) {
        this.parlourDetails = parlourDetails;
        this.parlourDistance = parlourDistance;
    }

    public ParlourModel getParlourDetails() {
        return parlourDetails;
    }

    public Double getParlourDistance() {
        return parlourDistance;
    }
}
