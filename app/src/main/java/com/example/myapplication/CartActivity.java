package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ListView listCartItems;
    private TextView textTotalPrice;
    private Button buttonCheckout;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    private FirebaseFirestore db;
    private String  userPassword;
    private String userRole;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listCartItems = findViewById(R.id.list_cart_items);
        textTotalPrice = findViewById(R.id.text_total_price);
        buttonCheckout = findViewById(R.id.button_checkout);
        db = FirebaseFirestore.getInstance();


        loadUserData();

        // קבלת פריטי הסל מ־CartManager
        cartItems = CartManager.getInstance().getCartItems();

        // Set adapter for cart items
        cartAdapter = new CartAdapter(this, cartItems);
        listCartItems.setAdapter(cartAdapter);

        // Calculate total price
        calculateTotalPrice();

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });
    }

    private void loadUserData() {

        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");
        Toast.makeText(this, "Checkout completed"+  userEmail , Toast.LENGTH_SHORT).show();


    }

    public void calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        textTotalPrice.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void checkout() {
        // Handle checkout logic (to be implemented)
        Toast.makeText(this, "Checkout completed", Toast.LENGTH_SHORT).show();

        // Save purchases to Firestore
        savePurchasesToFirestore();

        // Clear the cart
        CartManager.getInstance().clearCart();
        cartAdapter.notifyDataSetChanged();
        calculateTotalPrice();

        // Navigate back to MainActivity or any other appropriate screen
        Intent intent = new Intent(CartActivity.this, MainActivity.class);
        intent.putExtra("userRole", userRole);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userPassword", userPassword );

        startActivity(intent);
        finish();
    }

    private void savePurchasesToFirestore() {
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            Purchase purchase = new Purchase(userEmail, item.getName(), item.getPrice(), item.getQuantity());

            db.collection("purchases").add(purchase)
                    .addOnSuccessListener(documentReference -> {
                        // Handle successful write
                    })
                    .addOnFailureListener(e -> {
                        // Handle failed write
                    });
        }
    }
}
