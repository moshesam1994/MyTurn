package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends ArrayAdapter<CartItem> {
    public CartAdapter(Context context, List<CartItem> cartItems) {
        super(context, 0, cartItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CartItem cartItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cart, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        TextView textViewPrice = convertView.findViewById(R.id.text_view_price);
        TextView textViewQuantity = convertView.findViewById(R.id.text_view_quantity);
        Button buttonIncrease = convertView.findViewById(R.id.button_increase);
        Button buttonDecrease = convertView.findViewById(R.id.button_decrease);

        textViewName.setText(cartItem.getName());
        textViewPrice.setText(String.format("$%.2f", cartItem.getPrice()));
        textViewQuantity.setText(String.valueOf(cartItem.getQuantity()));

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                notifyDataSetChanged();
                ((CartActivity) getContext()).calculateTotalPrice();
            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    notifyDataSetChanged();
                    ((CartActivity) getContext()).calculateTotalPrice();
                }
            }
        });

        return convertView;
    }
}
