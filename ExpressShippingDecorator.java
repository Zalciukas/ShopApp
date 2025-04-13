package com.example.shopapp.model;

public class ExpressShippingDecorator extends OrderDecorator {
    public ExpressShippingDecorator(Order decoratedOrder) {
        super(decoratedOrder);
    }

    @Override
    public double calculateTotal() {
        return super.calculateTotal() + 10.0; // $10 for express shipping
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Express Shipping";
    }
}
