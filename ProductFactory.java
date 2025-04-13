package com.example.shopapp.model;

// FACTORY METHOD PATTERN
public interface ProductFactory {
    Product createProduct(String id, String name, double price, int quantity, String... additionalInfo);
}
