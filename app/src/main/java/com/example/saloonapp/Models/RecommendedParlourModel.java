package com.example.saloonapp.Models;

import java.io.Serializable;

public class RecommendedParlourModel {
    private ParlourModel parlourDetails;
    private Double parlourDistance;

    public RecommendedParlourModel(ParlourModel parlourDetails, Double parlourDistance) {
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
