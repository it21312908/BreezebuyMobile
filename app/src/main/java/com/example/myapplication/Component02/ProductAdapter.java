package com.example.myapplication.Component02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private List<Product> productList;
    private Context context;
    private OnProductCheckedChangeListener listener;

    public ProductAdapter(List<Product> productList, Context context, OnProductCheckedChangeListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        }

        TextView productName = convertView.findViewById(R.id.product_name);
        TextView productDescription = convertView.findViewById(R.id.product_description);
        TextView productPrice = convertView.findViewById(R.id.product_price);
        TextView quantityText = convertView.findViewById(R.id.quantity_edit_text);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        Button decreaseButton = convertView.findViewById(R.id.decrement_button);
        Button increaseButton = convertView.findViewById(R.id.increment_button);

        Product product = productList.get(position);
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());
        productPrice.setText(String.valueOf(product.getPrice()));
        quantityText.setText(String.valueOf(product.getQuantity()));
        checkBox.setChecked(product.isSelected());

        // Set listener for checkbox state change
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked);
            listener.onProductCheckedChanged(); // Notify the activity to recalculate total
        });

        // Set listeners for quantity buttons
        decreaseButton.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - 1);
                quantityText.setText(String.valueOf(product.getQuantity()));
                listener.onProductCheckedChanged(); // Notify the activity to recalculate total
            }
        });






        increaseButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            quantityText.setText(String.valueOf(product.getQuantity()));
            listener.onProductCheckedChanged(); // Notify the activity to recalculate total
        });

        return convertView;
    }

    public interface OnProductCheckedChangeListener {
        void onProductCheckedChanged();
    }
}