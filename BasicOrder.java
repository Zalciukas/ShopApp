package com.example.shopapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicOrder implements Order {
    private Map<Product, Integer> productQuantities;

    public BasicOrder() {
        this.productQuantities = new HashMap<>();
    }

    public void addProduct(Product product) {
        productQuantities.put(product, productQuantities.getOrDefault(product, 0) + 1);
    }

    public void removeProduct(Product product) {
        if (productQuantities.containsKey(product)) {
            int currentQty = productQuantities.get(product);
            if (currentQty > 1) {
                productQuantities.put(product, currentQty - 1);
            } else {
                productQuantities.remove(product);
            }
        }
    }

    public Map<Product, Integer> getProductQuantities() {
        return productQuantities;
    }

    @Override
    public double calculateTotal() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    @Override
    public String getDescription() {
        return "Basic order with " + getTotalItems() + " items";
    }

    public int getTotalItems() {
        int total = 0;
        for (Integer quantity : productQuantities.values()) {
            total += quantity;
        }
        return total;
    }
}