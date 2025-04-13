package com.example.shopapp.model;

// STRATEGY PATTERN
public interface PaymentStrategy {
    boolean processPayment(double amount);
    String getPaymentMethod();
}
