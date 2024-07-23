package com.example.myapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private Context context;
    private List<Purchase> purchaseList;

    public PurchaseAdapter(Context context, List<Purchase> purchaseList) {
        this.context = context;
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.textViewProductName.setText(purchase.getProductName());
        holder.textViewPrice.setText(String.format("$%.2f", purchase.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(purchase.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewQuantity;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.text_view_product_name);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            textViewQuantity = itemView.findViewById(R.id.text_view_quantity);
        }
    }
}
