package com.example.saloonapp.Models;

public class ServicesModel {
    private Integer serviceId;
    private String serviceName;

    public ServicesModel(Integer serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }
}
