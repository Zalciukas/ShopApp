package com.example.shopapp.model;

public class HomeAppliance extends Product {
    private String material;
    private String size;

    public HomeAppliance(String id, String name, double price, String material, String size, int quantity) {
        super(id, name, price, "Home Appliance", quantity);
        this.material = material;
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public String getSize() {
        return size;
    }
}
