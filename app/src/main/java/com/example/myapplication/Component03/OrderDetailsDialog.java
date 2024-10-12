package com.example.myapplication.Component03;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDialog extends Dialog {
    private String orderId;
    private String[] roles; // Variable to hold the user's roles
    private TextView textViewOrderId;
    private TextView textViewOrderNumber;
    private TextView textViewCustomer;
    private TextView textViewTotalPayment;
    private TextView textViewStatus;
    private TextView textViewCreatedAt;
    private Button buttonClose;
    private Button buttonUpdateStatus; // Button for updating status
    private RecyclerView recyclerViewItems;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private JSONObject currentOrderDetails;

    public OrderDetailsDialog(@NonNull Context context, String orderId, String[] roles) {
        super(context);
        this.orderId = orderId;
        this.roles = roles; // Store roles passed to the dialog
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_dialog);

        // Initialize TextViews and RecyclerView for each order detail
        textViewOrderId = findViewById(R.id.textViewOrderId);
        textViewOrderNumber = findViewById(R.id.textViewOrderNumber);
        textViewCustomer = findViewById(R.id.textViewCustomerId);
        textViewTotalPayment = findViewById(R.id.textViewTotalPayment);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewCreatedAt = findViewById(R.id.textViewCreatedAt);
        buttonClose = findViewById(R.id.buttonClose);
        buttonUpdateStatus = findViewById(R.id.buttonUpdateStatus); // Initialize the update status button
        recyclerViewItems = findViewById(R.id.recyclerViewItem); // RecyclerView for order items

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), itemList);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewItems.setAdapter(itemAdapter);

        fetchOrderDetails();

        buttonClose.setOnClickListener(v -> dismiss());

        buttonUpdateStatus.setOnClickListener(v -> {
            if (currentOrderDetails != null) { // Ensure currentOrderDetails is available
                updateOrderStatus(currentOrderDetails);
            } else {
                Toast.makeText(getContext(), "Current order details not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Show or hide the button based on the user's role
        if (roles != null && roles.length > 0) {
            boolean isCustomer = false;
            for (String role : roles) {
                if ("Customer".equalsIgnoreCase(role)) {
                    isCustomer = true;
                    break;
                }
            }

            if (isCustomer) {
                buttonUpdateStatus.setVisibility(View.GONE); // Hide the button if the user is a customer
            } else {
                buttonUpdateStatus.setVisibility(View.VISIBLE); // Show the button if the user is not a customer
            }
        }
    }

    private void fetchOrderDetails() {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Order/" + orderId;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse and display the order details
                            textViewOrderId.setText("Order ID: " + response.getString("id"));
                            textViewOrderNumber.setText("Order Number: " + response.getString("orderNumber"));
                            textViewCustomer.setText("Customer Id: " + response.getString("customerId"));
                            textViewTotalPayment.setText("Total Payment: $" + response.getDouble("totalPayment"));
                            textViewStatus.setText("Status: " + response.getString("status"));
                            textViewCreatedAt.setText("Created At: " + response.getString("createdAt"));

                            // Store current order details for later use
                            currentOrderDetails = response;

                            // Fetch items from the response
                            JSONArray itemsArray = response.getJSONArray("items");
                            itemList.clear(); // Clear the previous item list
                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject itemObject = itemsArray.getJSONObject(i);
                                Item item = new Item();
                                item.setProductId(itemObject.getString("productId"));
                                item.setQuantity(itemObject.getInt("quantity"));
                                item.setPrice(itemObject.getDouble("price"));
                                item.setTotalAmount(itemObject.getDouble("totalAmount"));
                                itemList.add(item);
                            }
                            itemAdapter.notifyDataSetChanged(); // Notify adapter of changes
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error parsing order details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error fetching order details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    // Method to update order status
    private void updateOrderStatus(JSONObject currentOrderDetails) {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Order/" + orderId; // Use the same URL for PUT request
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Create JSON object to send with the update
        JSONObject jsonObject = new JSONObject();
        try {
            // Include existing values from the currentOrderDetails
            jsonObject.put("OrderNumber", currentOrderDetails.getString("orderNumber"));
            jsonObject.put("Items", currentOrderDetails.getJSONArray("items"));

            // Update only the status
            jsonObject.put("status", "Delivered"); // Replace with the new status you want to set

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error creating JSON object: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Order status updated successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, you can refresh the order details here
                        fetchOrderDetails(); // Refresh details to show updated status
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error updating order status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}