package com.example.shopapp.model;

// DECORATOR PATTERN
public interface Order {
    double calculateTotal();
    String getDescription();
}
