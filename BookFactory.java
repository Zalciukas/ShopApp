package com.example.shopapp.model;

public class BookFactory implements ProductFactory {
    @Override
    public Product createProduct(String id, String name, double price, int quantity, String... additionalInfo) {
        return new Book(id, name, price, additionalInfo[0], Integer.parseInt(additionalInfo[1]), quantity);
    }
}
