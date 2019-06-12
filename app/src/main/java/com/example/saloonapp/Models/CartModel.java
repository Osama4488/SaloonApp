package com.example.saloonapp.Models;

public class CartModel {
    private String cartId;
    private String parlourName;
    private String parlourServiceName;
    private String parlourSubServiceName;
    private String parlourSubServicePrice;

    public CartModel(String cartId, String parlourName, String parlourServiceName, String parlourSubServiceName, String parlourSubServicePrice) {
        this.cartId = cartId;
        this.parlourName = parlourName;
        this.parlourServiceName = parlourServiceName;
        this.parlourSubServiceName = parlourSubServiceName;
        this.parlourSubServicePrice = parlourSubServicePrice;
    }

    public String getCartId() {
        return cartId;
    }

    public String getParlourName() {
        return parlourName;
    }

    public String getParlourServiceName() {
        return parlourServiceName;
    }

    public String getParlourSubServiceName() {
        return parlourSubServiceName;
    }

    public String getParlourSubServicePrice() {
        return parlourSubServicePrice;
    }
}
