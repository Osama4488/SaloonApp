package com.example.saloonapp.Models;

import java.util.List;

public class ParlourInformationModel {
    private String parlourId, parlourName, parlourEmail, parlourLatLng, parlourRating, parlourNumber;
    private List<ParlourTimingsModel> parlourTimingsModelList;

    public ParlourInformationModel(String parlourId, String parlourName, String parlourEmail, String parlourLatLng, String parlourRating, String parlourNumber, List<ParlourTimingsModel> parlourTimingsModelList) {
        this.parlourId = parlourId;
        this.parlourName = parlourName;
        this.parlourEmail = parlourEmail;
        this.parlourLatLng = parlourLatLng;
        this.parlourRating = parlourRating;
        this.parlourNumber = parlourNumber;
        this.parlourTimingsModelList = parlourTimingsModelList;
    }

    public List<ParlourTimingsModel> getParlourTimingsModelList() {
        return parlourTimingsModelList;
    }

    public String getParlourId() {
        return parlourId;
    }

    public String getParlourName() {
        return parlourName;
    }

    public String getParlourEmail() {
        return parlourEmail;
    }

    public String getParlourLatLng() {
        return parlourLatLng;
    }

    public String getParlourRating() {
        return parlourRating;
    }

    public String getParlourNumber() {
        return parlourNumber;
    }
}
