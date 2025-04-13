package com.example.shopapp.model;

public class ElectronicsFactory implements ProductFactory {
    @Override
    public Product createProduct(String id, String name, double price, int quantity, String... additionalInfo) {
        return new Electronics(id, name, price, additionalInfo[0], additionalInfo[1], quantity);
    }
}
