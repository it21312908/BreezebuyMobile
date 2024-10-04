package com.example.myapplication.Component03;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);

        // Initialize the order list (hard-coded for this example)
        orderList = getOrders();

        // Set up the RecyclerView
        orderAdapter = new OrderAdapter(orderList);
        recyclerViewOrders.setAdapter(orderAdapter);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        // Sample data
        orders.add(new Order("1001", "Shipped", "2024-10-01"));
        orders.add(new Order("1002", "In Transit", "2024-10-02"));
        orders.add(new Order("1003", "Delivered", "2024-09-30"));
        return orders; // Return your order list
    }
}