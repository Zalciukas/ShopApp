package com.example.shopapp;

import com.example.shopapp.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManagerController implements Initializable, InventoryObserver {
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, String> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private Label totalSalesLabel;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextArea notificationArea;

    private Inventory inventory;
    private ObservableList<Product> productList;
    private NotificationManager notificationManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize inventory (Singleton) and register as observer
        inventory = Inventory.getInstance();
        inventory.addObserver(this);

        // Initialize notification manager
        notificationManager = NotificationManager.getInstance();

        // Initialize observable list
        productList = FXCollections.observableArrayList();

        // Set up TableView
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        productTableView.setItems(productList);

        // Fill category combo box
        categoryComboBox.getItems().addAll("All", "Book", "Electronics", "Home Appliance");
        categoryComboBox.setValue("All");

        // Load all products initially
        loadProducts();

        // Update total sales label
        updateTotalSales();

        // Load existing notifications
        loadNotifications();
    }

    private void loadNotifications() {
        notificationArea.setText(notificationManager.getNotificationsAsString());
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

    private void updateTotalSales() {
        totalSalesLabel.setText(String.format("Total Sales: $%.2f", inventory.getTotalSales()));
    }

    @FXML
    private void onCategoryChanged() {
        loadProducts();
    }

    @FXML
    private void onAddProductClicked() {
        // Create a dialog for product input
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter Product Details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("Product ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll("Book", "Electronics", "Home Appliance");
        categoryField.setPromptText("Category");

        // Add fields to the grid
        grid.add(new Label("Product ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Quantity:"), 0, 4);
        grid.add(quantityField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        nameField.requestFocus();

        // Convert the result to a product when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    // Validate required fields
                    if (idField.getText().isEmpty() || nameField.getText().isEmpty() ||
                            priceField.getText().isEmpty() || quantityField.getText().isEmpty() ||
                            categoryField.getValue() == null) {
                        showAlert("Validation Error", "All fields are required.");
                        return null;
                    }

                    String id = idField.getText();
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    String category = categoryField.getValue();

                    // Create the appropriate product based on category
                    ProductFactory factory;
                    String[] additionalInfo = new String[2];

                    switch (category) {
                        case "Book":
                            factory = new BookFactory();
                            additionalInfo[0] = "Unknown"; // Default author
                            additionalInfo[1] = "0";       // Default pages
                            break;
                        case "Electronics":
                            factory = new ElectronicsFactory();
                            additionalInfo[0] = "Generic"; // Default brand
                            additionalInfo[1] = "None";    // Default warranty
                            break;
                        case "Home Appliance":
                            factory = new HomeApplianceFactory();
                            additionalInfo[0] = "Standard"; // Default material
                            additionalInfo[1] = "Medium";   // Default size
                            break;
                        default:
                            return null;
                    }

                    return factory.createProduct(id, name, price, quantity, additionalInfo);

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for price and quantity.");
                    return null;
                }
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            inventory.addProduct(product);
            loadProducts();
        });
    }

    @FXML
    private void onUpdateProductClicked() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("No Selection", "Please select a product to update.");
            return;
        }

        // Create a dialog for quantity update
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Update Product Quantity");
        dialog.setHeaderText("Enter new quantity for " + selectedProduct.getName());

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the quantity field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField quantityField = new TextField(String.valueOf(selectedProduct.getQuantity()));
        quantityField.setPromptText("New Quantity");

        grid.add(new Label("Current Quantity:"), 0, 0);
        grid.add(new Label(String.valueOf(selectedProduct.getQuantity())), 1, 0);
        grid.add(new Label("New Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the quantity field
        quantityField.requestFocus();

        // Convert the result when the update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    return Integer.parseInt(quantityField.getText());
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid number for quantity.");
                    return null;
                }
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(newQuantity -> {
            inventory.updateProductQuantity(selectedProduct.getId(), newQuantity);
            loadProducts();
        });
    }

    @FXML
    private void onRemoveProductClicked() {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("No Selection", "Please select a product to remove.");
            return;
        }

        // Confirm before deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Product");
        alert.setContentText("Are you sure you want to delete " + selectedProduct.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            inventory.removeProduct(selectedProduct.getId());
            loadProducts();
        }
    }

    @FXML
    private void onClearNotificationsClicked() {
        notificationManager.clearNotifications();
        notificationArea.clear();
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
        String notification;
        if (eventType.equals("NEW_PRODUCT")) {
            notification = "New product added: " + product.getName() +
                    " - $" + product.getPrice() +
                    " (" + product.getCategory() + ")\n";
        } else if (eventType.equals("PURCHASE")) {
            notification = "Product purchased: " + product.getName() +
                    " - $" + product.getPrice() +
                    " (" + product.getCategory() + ")\n";
        } else {
            notification = "Inventory update: " + product.getName() + "\n";
        }

        // Add to the notification manager
        notificationManager.addNotification(notification);

        // Using Platform.runLater to update UI from a different thread
        javafx.application.Platform.runLater(() -> {
            notificationArea.appendText(notification);

            // Refresh the product list
            loadProducts();

            // Update total sales display
            updateTotalSales();
        });
    }

    @FXML
    private Button logoutButton;

    @FXML
    private Button clearNotificationsButton;

    @FXML
    private void onLogoutButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Shop Login");
            stage.setScene(new Scene(root, 500, 300));
            stage.show();

            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
