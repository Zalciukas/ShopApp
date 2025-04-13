package com.example.shopapp.model;

public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String name;
    private String expiryDate;
    private String cvv;

    public CreditCardPayment(String cardNumber, String name, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public boolean processPayment(double amount) {
        // In a real app, this would make an API call to a payment processor
        System.out.println("Processing credit card payment of $" + amount);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "Credit Card";
    }
}
