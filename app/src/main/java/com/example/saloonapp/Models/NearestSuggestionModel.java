package com.example.saloonapp.Models;

public class NearestSuggestionModel {
    private SuggestionModel parlourDetails;
    private Double parlourDistance;

    public NearestSuggestionModel(SuggestionModel parlourDetails, Double parlourDistance) {
        this.parlourDetails = parlourDetails;
        this.parlourDistance = parlourDistance;
    }

    public SuggestionModel getParlourDetails() {
        return parlourDetails;
    }

    public Double getParlourDistance() {
        return parlourDistance;
    }
}
