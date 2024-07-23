package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private boolean isManager;
    private FirebaseFirestore db;

    public ProductAdapter(Context context, List<Product> productList, boolean isManager) {
        this.context = context;
        this.productList = productList;
        this.isManager = isManager;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("$%.2f", product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).into(holder.imageViewProduct);

        holder.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the product to cart
                ((ProductActivity) context).addToCart(product);
            }
        });

        if (isManager) {
            holder.buttonDeleteProduct.setVisibility(View.VISIBLE);
            holder.buttonDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteProduct(product);
                }
            });
        } else {
            holder.buttonDeleteProduct.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPrice;
        ImageView imageViewProduct;
        Button buttonAddToCart;
        Button buttonDeleteProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            imageViewProduct = itemView.findViewById(R.id.image_view_product);
            buttonAddToCart = itemView.findViewById(R.id.button_add_to_cart);
            buttonDeleteProduct = itemView.findViewById(R.id.button_delete_product);
        }
    }

    private void deleteProduct(Product product) {
        // Delete the product from Firebase Firestore
        db.collection("products").document(product.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
