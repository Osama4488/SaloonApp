package com.example.saloonapp.Models;

public class ExpertsModel {
    private Integer expertId;
    private String expertName;
    private String expertExpertise;
    private String expertExperience;

    public ExpertsModel(Integer expertId, String expertName, String expertExpertise, String expertExperience) {
        this.expertId = expertId;
        this.expertName = expertName;
        this.expertExpertise = expertExpertise;
        this.expertExperience = expertExperience;
    }

    public Integer getExpertId() {
        return expertId;
    }

    public String getExpertName() {
        return expertName;
    }

    public String getExpertExpertise() {
        return expertExpertise;
    }

    public String getExpertExperience() {
        return expertExperience;
    }
}
