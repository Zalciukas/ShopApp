package com.example.shopapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// SINGLETON PATTERN
public class Inventory {
    private static Inventory instance;
    private Map<String, Product> products;
    private List<InventoryObserver> observers;
    private double totalSales;

    private Inventory() {
        products = new HashMap<>();
        observers = new ArrayList<>();
        totalSales = 0.0;

        // Add some sample products
        initializeSampleProducts();
    }

    private void initializeSampleProducts() {
        // Creating sample products using factories
        ProductFactory bookFactory = new BookFactory();
        ProductFactory electronicsFactory = new ElectronicsFactory();
        ProductFactory homeApplianceFactory = new HomeApplianceFactory();

        Product book1 = bookFactory.createProduct("B001", "Java Programming", 29.99, 5, "John Doe", "450");
        Product book2 = bookFactory.createProduct("B002", "Design Patterns", 39.99, 3, "Gang of Four", "395");
        Product electronics1 = electronicsFactory.createProduct("E001", "Smartphone", 599.99, 10, "TechBrand", "2 years");
        Product electronics2 = electronicsFactory.createProduct("E002", "Laptop", 999.99, 7, "ComputerBrand", "3 years");
        Product homeAppliance1 = homeApplianceFactory.createProduct("H001", "Coffee Maker", 79.99, 15, "Stainless Steel", "Medium");
        Product homeAppliance2 = homeApplianceFactory.createProduct("H002", "Blender", 49.99, 20, "Plastic", "Small");

        addProduct(book1);
        addProduct(book2);
        addProduct(electronics1);
        addProduct(electronics2);
        addProduct(homeAppliance1);
        addProduct(homeAppliance2);
    }

    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public void addProduct(Product product) {
        String productId = product.getId();

        if (products.containsKey(productId)) {
            // If product exists, increase quantity
            Product existingProduct = products.get(productId);
            existingProduct.increaseQuantity(product.getQuantity());
        } else {
            // Otherwise add new product
            products.put(productId, product);
            notifyObservers(product, "NEW_PRODUCT");
        }
    }

    public void removeProduct(String productId) {
        if (products.containsKey(productId)) {
            products.remove(productId);
            // Could notify observers about removal if needed
        }
    }

    public void updateProductQuantity(String productId, int newQuantity) {
        if (products.containsKey(productId)) {
            Product product = products.get(productId);
            product.setQuantity(newQuantity);

            // Remove if quantity is zero
            if (newQuantity <= 0) {
                products.remove(productId);
            }
        }
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getCategory().equals(category)) {
                result.add(product);
            }
        }
        return result;
    }

    public void recordSale(double amount) {
        totalSales += amount;
    }

    public double getTotalSales() {
        return totalSales;
    }

    // OBSERVER PATTERN
    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(InventoryObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Product product, String eventType) {
        for (InventoryObserver observer : observers) {
            observer.update(product, eventType);
        }
    }

    public void notifyPurchase(Product product, int quantity) {
        notifyObservers(product, "PURCHASE");
    }
}
