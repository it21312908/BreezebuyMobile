package com.example.myapplication.Component02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.myapplication.R;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private List<OrderItem> orderItemList;
    private Context context;

    public CartAdapter(List<OrderItem> orderItemList, Context context) {
        this.orderItemList = orderItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        }

        OrderItem orderItem = orderItemList.get(position);

        TextView productIdText = convertView.findViewById(R.id.product_id_text);
        TextView productPriceText = convertView.findViewById(R.id.product_price_text);
        TextView productQuantityText = convertView.findViewById(R.id.product_quantity_text);
        TextView productTotalText = convertView.findViewById(R.id.product_total_text);

        productIdText.setText("Product ID: " + orderItem.getProductId());
        productPriceText.setText("Price: $" + String.format("%.2f", orderItem.getPrice()));
        productQuantityText.setText("Quantity: " + orderItem.getQuantity());
        productTotalText.setText("Total Amount: $" + String.format("%.2f", orderItem.getTotalAmount()));


        return convertView;
    }
}