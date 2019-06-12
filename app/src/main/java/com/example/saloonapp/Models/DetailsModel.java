package com.example.saloonapp.Models;

public class DetailsModel {
    private String subServieName, subServicePrice, subServiceQty;

    public DetailsModel(String subServieName, String subServicePrice, String subServiceQty) {
        this.subServieName = subServieName;
        this.subServicePrice = subServicePrice;
        this.subServiceQty = subServiceQty;
    }

    public String getSubServieName() {
        return subServieName;
    }

    public String getSubServicePrice() {
        return subServicePrice;
    }

    public String getSubServiceQty() {
        return subServiceQty;
    }
}
