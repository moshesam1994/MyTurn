package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProductToCart(Product product) {
        for (CartItem item : cartItems) {
            if (item.getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(product.getName(), product.getPrice(), 1));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }
}