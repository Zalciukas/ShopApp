package com.example.shopapp.model;

import java.util.Map;

// Observer interface
public interface InventoryObserver {
    void update(Product product, String eventType);
}
