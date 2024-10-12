package com.example.myapplication.Component03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final Context context;
    private final List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewProductId.setText("Product ID: " + item.getProductId());
        holder.textViewQuantity.setText("Quantity: " + item.getQuantity());
        holder.textViewPrice.setText("Price: $" + item.getPrice());
        holder.textViewTotalAmount.setText("Total: $" + item.getTotalAmount());

        // Display the product name
        if (item.getProductName() != null) {
            holder.textViewProductName.setText("Product Name: " + item.getProductName());
        } else {
            holder.textViewProductName.setText("Product Name: Loading...");
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductId;
        TextView textViewProductName;  // Add this line
        TextView textViewQuantity;
        TextView textViewPrice;
        TextView textViewTotalAmount;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductId = itemView.findViewById(R.id.textViewProductId);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);  // Add this line
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
        }
    }
}