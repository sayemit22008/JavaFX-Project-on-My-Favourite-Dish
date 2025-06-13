package com.example.my_favourite_dish;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class DishController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TableView<FoodItem> dishTable;

    @FXML
    private TableColumn<FoodItem, String> nameColumn;

    @FXML
    private TableColumn<FoodItem, String> descriptionColumn;

    private ObservableList<FoodItem> dishes = FXCollections.observableArrayList();

    private Connection connection;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        dishTable.setItems(dishes);
        connectToDatabase();
        loadDishesFromDatabase();

        dishTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                descriptionField.setText(newSelection.getDescription());
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/food_db", "Atif", "arpita");
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS dishes (name VARCHAR(255), description TEXT)");
        } catch (SQLException e) {
            showAlert("ডেটাবেইস ত্রুটি", "ডেটাবেইসে সংযোগ করা যায়নি: " + e.getMessage()); // "Database Error"
        }
    }

    private void loadDishesFromDatabase() {
        dishes.clear();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM dishes");
            while (rs.next()) {
                dishes.add(new FoodItem(rs.getString("name"), rs.getString("description")));
            }
        } catch (SQLException e) {
            showAlert("ত্রুটি", "তথ্য লোড করা যায়নি: " + e.getMessage()); // "Error"
        }
    }

    @FXML
    protected void addDish() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            showAlert("ত্রুটি", "খাবারের নাম দিতে হবে"); // "Error" -> "Dish name is required"
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO dishes (name, description) VALUES (?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("ত্রুটি", "খাবার যোগ করা যায়নি: " + e.getMessage()); // "Could not add dish"
            return;
        }

        dishes.add(new FoodItem(name, description));
        clearFields();
    }

    @FXML
    protected void updateDish() {
        FoodItem selected = dishTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("তথ্য নেই", "আপডেট করার জন্য একটি আইটেম নির্বাচন করুন"); // "No item selected"
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE dishes SET name = ?, description = ? WHERE name = ?");
            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, descriptionField.getText().trim());
            stmt.setString(3, selected.getName());
            stmt.executeUpdate();

            selected.setName(nameField.getText().trim());
            selected.setDescription(descriptionField.getText().trim());
            dishTable.refresh();
        } catch (SQLException e) {
            showAlert("ত্রুটি", "খাবার আপডেট করা যায়নি: " + e.getMessage()); // "Could not update dish"
        }

        clearFields();
    }

    @FXML
    protected void deleteDish() {
        FoodItem selected = dishTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("তথ্য নেই", "মুছে ফেলার জন্য একটি আইটেম নির্বাচন করুন"); // "No item selected"
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM dishes WHERE name = ?");
            stmt.setString(1, selected.getName());
            stmt.executeUpdate();

            dishes.remove(selected);
        } catch (SQLException e) {
            showAlert("ত্রুটি", "খাবার মুছে ফেলা যায়নি: " + e.getMessage()); // "Could not delete dish"
        }

        clearFields();
    }

    @FXML
    public void clearFields() {
        nameField.clear();
        descriptionField.clear();
        dishTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
