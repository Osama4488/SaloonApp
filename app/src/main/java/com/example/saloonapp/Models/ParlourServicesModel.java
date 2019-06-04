package com.example.saloonapp.Models;

import java.util.List;

public class ParlourServicesModel {
    private ServicesModel service;
    private List<SubServicesModel> subServiceList;

    public ParlourServicesModel(ServicesModel service, List<SubServicesModel> subServiceList) {
        this.service = service;
        this.subServiceList = subServiceList;
    }

    public ServicesModel getService() {
        return service;
    }

    public List<SubServicesModel> getSubServiceList() {
        return subServiceList;
    }
}
