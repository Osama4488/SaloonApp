package com.example.saloonapp.Models;

public class SubServicesModel {
    private String subServiceId, subServiceName, subServiceDescription, serviceId;
    private Integer subServicePrice;

    public SubServicesModel(String subServiceId, String subServiceName, String subServiceDescription, String serviceId, Integer subServicePrice) {
        this.subServiceId = subServiceId;
        this.subServiceName = subServiceName;
        this.subServiceDescription = subServiceDescription;
        this.serviceId = serviceId;
        this.subServicePrice = subServicePrice;
    }

    public String getSubServiceId() {
        return subServiceId;
    }

    public String getServiceId() {
        return serviceId;
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
