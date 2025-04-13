package com.example.shopapp.model;

public class InsuranceDecorator extends OrderDecorator {
    public InsuranceDecorator(Order decoratedOrder) {
        super(decoratedOrder);
    }

    @Override
    public double calculateTotal() {
        return super.calculateTotal() + 7.5; // $7.50 for insurance
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Insurance";
    }
}
