package com.example.shopapp.model;

public class GiftWrappingDecorator extends OrderDecorator {
    public GiftWrappingDecorator(Order decoratedOrder) {
        super(decoratedOrder);
    }

    @Override
    public double calculateTotal() {
        return super.calculateTotal() + 5.0; // $5 for gift wrapping
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Gift Wrapping";
    }
}
