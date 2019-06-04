package com.example.saloonapp.Models;

public class ServicesModel {
    private String serviceId;
    private String serviceName;

    public ServicesModel(String serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }
}
