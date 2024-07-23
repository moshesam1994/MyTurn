package com.example.myapplication;

public class Purchase {
    private String email;
    private String productName;
    private double price;
    private int quantity;

    public Purchase() {
        // Default constructor required for calls to DataSnapshot.getValue(Purchase.class)
    }

    public Purchase(String email, String productName, double price, int quantity) {
        this.email = email;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
