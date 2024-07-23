package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView listProducts;
    private Button buttonCart;
    private Button buttonAddProduct;
    private List<Product> products;
    private ProductAdapter adapter;
    private static final int ADD_PRODUCT_REQUEST = 2;
    private String userRole;
    private FirebaseFirestore db;
    private String userEmail;
    private String  userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        listProducts = findViewById(R.id.list_products);
        buttonCart = findViewById(R.id.button_cart);
        buttonAddProduct = findViewById(R.id.button_add_product);

        userRole = getIntent().getStringExtra("userRole");
        db = FirebaseFirestore.getInstance();
        products = new ArrayList<>();

        Intent intent = getIntent();
        userRole = intent.getStringExtra("userRole");
        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");
        adapter = new ProductAdapter(this, products, "manager".equals(userRole));
        listProducts.setLayoutManager(new LinearLayoutManager(this));
        listProducts.setAdapter(adapter);

        loadProductsFromDB();

        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                intent.putExtra("userEmail", userEmail);

                intent.putExtra("userRole", userRole);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userPassword", userPassword );

                startActivity(intent);
            }
        });

        if ("manager".equals(userRole)) {
            buttonAddProduct.setVisibility(View.VISIBLE);
        } else {
            buttonAddProduct.setVisibility(View.GONE);
        }

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);

                startActivityForResult(intent, ADD_PRODUCT_REQUEST);
            }
        });
    }

    private void loadProductsFromDB() {
        db.collection("products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(ProductActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
                    return;
                }
                products.clear();
                if (value != null) {
                    for (DocumentSnapshot doc : value) {
                        Product product = doc.toObject(Product.class);
                        product.setId(doc.getId());
                        products.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            loadProductsFromDB();
        }
    }

    public void addToCart(Product product) {
        CartManager.getInstance().addProductToCart(product);
        Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("update-cart");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
