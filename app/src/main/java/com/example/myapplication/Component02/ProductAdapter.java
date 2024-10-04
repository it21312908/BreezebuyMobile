package com.example.myapplication.Component02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
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
        holder.productName.setText(product.getName());
        holder.productPrice.setText("$" + product.getPrice());
        holder.productDescription.setText(product.getDescription());
        holder.productQuantity.setText("Quantity: " + product.getQuantity()); // Display the quantity
        holder.productCategoryId.setText("Category ID: " + product.getCategoryId()); // Display the category ID

        // Handle purchase button click
        holder.purchaseButton.setOnClickListener(v -> {
            if (context instanceof ProductBrowsingActivity) {
                ((ProductBrowsingActivity) context).markAsPurchased(product); // Call markAsPurchased method
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription, productQuantity, productCategoryId;
        Button purchaseButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            productQuantity = itemView.findViewById(R.id.productQuantity); // Added for quantity
            productCategoryId = itemView.findViewById(R.id.productCategoryId); // Added for category ID
            purchaseButton = itemView.findViewById(R.id.purchaseButton);
        }
    }
}