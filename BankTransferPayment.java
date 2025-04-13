package com.example.shopapp.model;

public class BankTransferPayment implements PaymentStrategy {
    private String accountNumber;
    private String bankCode;

    public BankTransferPayment(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

    @Override
    public boolean processPayment(double amount) {
        // In a real app, this would make an API call to a bank
        System.out.println("Processing bank transfer payment of $" + amount);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "Bank Transfer";
    }
}
