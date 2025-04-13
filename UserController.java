package com.example.shopapp;

import com.example.shopapp.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class UserController implements Initializable, InventoryObserver {
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TableView<CartItem> cartTableView;
    @FXML
    private TableColumn<CartItem, String> cartProductColumn;
    @FXML
    private TableColumn<CartItem, Integer> cartQuantityColumn;
    @FXML
    private TableColumn<CartItem, Double> cartPriceColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private CheckBox giftWrappingCheckBox;
    @FXML
    private CheckBox expressShippingCheckBox;
    @FXML
    private CheckBox insuranceCheckBox;
    @FXML
    private ComboBox<String> paymentMethodComboBox;

    private Inventory inventory;
    private BasicOrder currentOrder;
    private ObservableList<Product> productList;
    private ObservableList<CartItem> cartItemsList;

    // Helper class to display cart items
    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public String getProductName() {
            return product.getName();
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotalPrice() {
            return product.getPrice() * quantity;
        }

        public Product getProduct() {
            return product;
        }

        public void incrementQuantity() {
            quantity++;
        }

        public void decrementQuantity() {
            if (quantity > 0) {
                quantity--;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize inventory (Singleton) and register as observer
        inventory = Inventory.getInstance();
        inventory.addObserver(this);

        // Initialize order
        currentOrder = new BasicOrder();

        // Initialize lists
        productList = FXCollections.observableArrayList();
        cartItemsList = FXCollections.observableArrayList();

        // Set up product TableView
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        productTableView.setItems(productList);

        // Set up cart TableView
        cartProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        cartTableView.setItems(cartItemsList);

        // Fill category combo box
        categoryComboBox.getItems().addAll("All", "Book", "Electronics", "Home Appliance");
        categoryComboBox.setValue("All");

        // Fill payment method combo box
        paymentMethodComboBox.getItems().addAll("Credit Card", "PayPal", "Bank Transfer");
        paymentMethodComboBox.setValue("Credit Card");

        // Load all products initially
        loadProducts();
    }

    private void loadProducts() {
        productList.clear();
        String selectedCategory = categoryComboBox.getValue();

        if ("All".equals(selectedCategory)) {
            productList.addAll(inventory.getAllProducts());
        } else {
            productList.addAll(inventory.getProductsByCategory(selectedCategory));
        }
    }

    @FXML
    private void onCategoryChanged() {
        loadProducts();
    }

    @FXML
    private void onAddToCartClicked() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            if (selectedProduct.getQuantity() > 0) {
                // Check if this product is already in the cart
                boolean productFound = false;
                for (CartItem item : cartItemsList) {
                    if (item.getProduct().getId().equals(selectedProduct.getId())) {
                        if (item.getQuantity() < selectedProduct.getQuantity()) {
                            item.incrementQuantity();
                            currentOrder.addProduct(selectedProduct);
                            productFound = true;

                            // Add this line to update the cart table
                            cartTableView.refresh();
                        } else {
                            showAlert("Not enough stock",
                                    "There are only " + selectedProduct.getQuantity() +
                                            " items of " + selectedProduct.getName() + " available.");
                            return;
                        }
                    }

                }

                if (!productFound) {
                    CartItem newItem = new CartItem(selectedProduct, 1);
                    cartItemsList.add(newItem);
                    currentOrder.addProduct(selectedProduct);
                }

                updateTotal();
            } else {
                showAlert("Out of stock", "This product is out of stock.");
            }
        }
    }

    @FXML
    private void onRemoveFromCartClicked() {
        CartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Product product = selectedItem.getProduct();

            // Update the item quantity
            selectedItem.decrementQuantity();

            // If quantity is now 0, remove from list
            if (selectedItem.getQuantity() <= 0) {
                cartItemsList.remove(selectedItem);
                cartTableView.refresh();
            }

            // Remove one product from order
            currentOrder.removeProduct(product);

            // Update the total
            updateTotal();
            cartTableView.refresh();
        }
    }

    @FXML
    private void onDecoratorChanged() {
        updateTotal();
    }

    private void updateTotal() {
        Order decoratedOrder = currentOrder;

        // Apply decorators based on checkboxes
        if (giftWrappingCheckBox.isSelected()) {
            decoratedOrder = new GiftWrappingDecorator(decoratedOrder);
        }

        if (expressShippingCheckBox.isSelected()) {
            decoratedOrder = new ExpressShippingDecorator(decoratedOrder);
        }

        if (insuranceCheckBox.isSelected()) {
            decoratedOrder = new InsuranceDecorator(decoratedOrder);
        }

        totalLabel.setText(String.format("Total: $%.2f - %s",
                decoratedOrder.calculateTotal(),
                decoratedOrder.getDescription()));
    }

    @FXML
    private void onProcessPayment() {
        if (cartItemsList.isEmpty()) {
            showAlert("Cart is empty", "Please add products to your cart before checkout.");
            return;
        }

        // Create the appropriate payment strategy based on selection
        PaymentStrategy paymentStrategy;
        String selectedMethod = paymentMethodComboBox.getValue();

        switch (selectedMethod) {
            case "Credit Card":
                paymentStrategy = new CreditCardPayment("1234-5678-9012-3456", "John Doe", "12/25", "123");
                break;
            case "PayPal":
                paymentStrategy = new PayPalPayment("user@example.com", "password");
                break;
            case "Bank Transfer":
                paymentStrategy = new BankTransferPayment("987654321", "BANKCODE123");
                break;
            default:
                paymentStrategy = new CreditCardPayment("1234-5678-9012-3456", "John Doe", "12/25", "123");
        }

        // Process the payment
        Order decoratedOrder = currentOrder;
        if (giftWrappingCheckBox.isSelected()) {
            decoratedOrder = new GiftWrappingDecorator(decoratedOrder);
        }
        if (expressShippingCheckBox.isSelected()) {
            decoratedOrder = new ExpressShippingDecorator(decoratedOrder);
        }
        if (insuranceCheckBox.isSelected()) {
            decoratedOrder = new InsuranceDecorator(decoratedOrder);
        }

        double total = decoratedOrder.calculateTotal();
        boolean success = paymentStrategy.processPayment(total);

        if (success) {
            // Update inventory quantities
            try {
                for (CartItem item : cartItemsList) {
                    Product product = item.getProduct();
                    int quantity = item.getQuantity();
                    product.decreaseQuantity(quantity);

                    // Notify purchase
                    inventory.notifyPurchase(product, quantity);
                }

                // Record the sale in the inventory
                inventory.recordSale(total);

                showAlert("Payment Successful",
                        String.format("Payment of $%.2f processed successfully using %s.\n%s",
                                total,
                                paymentStrategy.getPaymentMethod(),
                                decoratedOrder.getDescription()));

                // Reset cart and order
                cartItemsList.clear();
                currentOrder = new BasicOrder();
                updateTotal();
                giftWrappingCheckBox.setSelected(false);
                expressShippingCheckBox.setSelected(false);
                insuranceCheckBox.setSelected(false);

                // Refresh product list to show updated quantities
                loadProducts();

            } catch (IllegalArgumentException e) {
                showAlert("Inventory Error", "There was an error updating inventory: " + e.getMessage());
            }
        } else {
            showAlert("Payment Failed", "There was an error processing your payment. Please try again.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Observer pattern implementation
    @Override
    public void update(Product product, String eventType) {
        // In user view, we're mostly interested in product updates
        // Refresh the product list
        loadProducts();
    }

    @FXML
    private Button logoutButton;

    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Shop Login");
            stage.setScene(new Scene(root, 500, 300));
            stage.show();

            // Close the current window
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}