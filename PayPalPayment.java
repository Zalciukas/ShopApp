package com.example.shopapp.model;

public class PayPalPayment implements PaymentStrategy {
    private String email;
    private String password;

    public PayPalPayment(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean processPayment(double amount) {
        // In a real app, this would make an API call to PayPal
        System.out.println("Processing PayPal payment of $" + amount);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "PayPal";
    }
}
