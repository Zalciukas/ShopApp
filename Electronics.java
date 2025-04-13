package com.example.shopapp.model;

public class Electronics extends Product {
    private String brand;
    private String warranty;

    public Electronics(String id, String name, double price, String brand, String warranty, int quantity) {
        super(id, name, price, "Electronics", quantity);
        this.brand = brand;
        this.warranty = warranty;
    }

    public String getBrand() {
        return brand;
    }

    public String getWarranty() {
        return warranty;
    }
}
