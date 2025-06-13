package com.example.my_favourite_dish;

public class FoodItem {
    private String name;
    private String description;

    public FoodItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters & setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
