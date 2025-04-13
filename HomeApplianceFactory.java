package com.example.shopapp.model;

public class HomeApplianceFactory implements ProductFactory {
    @Override
    public Product createProduct(String id, String name, double price, int quantity, String... additionalInfo) {
        return new HomeAppliance(id, name, price, additionalInfo[0], additionalInfo[1], quantity);
    }
}
