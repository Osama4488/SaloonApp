package com.example.saloonapp.Models;

public class SubServicesModel {
    private Integer subServiceId;
    private String subServiceName;
    private String subServiceDescription;
    private Integer subServicePrice;

    public SubServicesModel(Integer subServiceId, String subServiceName, String subServiceDescription, Integer subServicePrice) {
        this.subServiceId = subServiceId;
        this.subServiceName = subServiceName;
        this.subServiceDescription = subServiceDescription;
        this.subServicePrice = subServicePrice;
    }

    public Integer getSubServiceId() {
        return subServiceId;
    }

    public String getSubServiceName() {
        return subServiceName;
    }

    public String getSubServiceDescription() {
        return subServiceDescription;
    }

    public Integer getSubServicePrice() {
        return subServicePrice;
    }
}
