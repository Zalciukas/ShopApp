package com.example.shopapp.model;

public abstract class OrderDecorator implements Order {
    protected Order decoratedOrder;

    public OrderDecorator(Order decoratedOrder) {
        this.decoratedOrder = decoratedOrder;
    }

    @Override
    public double calculateTotal() {
        return decoratedOrder.calculateTotal();
    }

    @Override
    public String getDescription() {
        return decoratedOrder.getDescription();
    }
}
