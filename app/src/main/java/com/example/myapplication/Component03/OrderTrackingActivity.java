package com.example.myapplication.Component03;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.Component01.UserProfileActivity;
import com.example.myapplication.Component02.ProductBrowsingActivity;
import com.example.myapplication.Component02.VolleySingleton;
import com.example.myapplication.Component04.ViewAllVendors;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private String userId; // Store user ID for API call
    private String currentEmail; // Store current email for the request
    private String token; // Store token for authentication
    private String[] roles; // Store user role
    private static final String EXTRA_ROLES = "ROLES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        // Retrieve userId, role, and token from Intent
        Intent intent = getIntent();
        String userId = intent.getStringExtra("USER_ID");
        String token = intent.getStringExtra("TOKEN");
        String[] roles = intent.getStringArrayExtra(EXTRA_ROLES);
        String username = intent.getStringExtra("USERNAME");
        String currentEmail = intent.getStringExtra("EMAIL");

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Set the order click listener to show details in a dialog
        orderAdapter.setOnOrderClickListener(order -> {
            OrderDetailsDialog orderDetailsDialog = new OrderDetailsDialog(this, order.getId(), roles); // Pass roles array
            orderDetailsDialog.show();
        });

        // Fetch order data from API
        fetchOrderData();

        // Set up bottom navigation
        setupBottomNavigation(userId, token, username, currentEmail, roles); // Pass userRole
    }

    private void fetchOrderData() {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Order";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Order order = new Order();
                                order.setId(jsonObject.getString("id"));
                                order.setOrderNumber(jsonObject.getString("orderNumber"));
                                order.setCustomerId(jsonObject.getString("customerId"));
                                order.setStatus(jsonObject.getString("status"));
                                order.setTotalPayment(jsonObject.getDouble("totalPayment"));

                                // Parse items
                                List<Item> itemList = new ArrayList<>();
                                JSONArray itemsArray = jsonObject.getJSONArray("items");
                                for (int j = 0; j < itemsArray.length(); j++) {
                                    JSONObject itemObject = itemsArray.getJSONObject(j);
                                    Item item = new Item();
                                    item.setProductId(itemObject.getString("productId"));
                                    item.setQuantity(itemObject.getInt("quantity"));
                                    item.setPrice(itemObject.getDouble("price"));
                                    item.setTotalAmount(itemObject.getDouble("totalAmount"));

                                    // Fetch product name
                                    fetchProductName(item.getProductId(), item);

                                    itemList.add(item);
                                }
                                order.setItems(itemList); // Set items to order
                                orderList.add(order);
                            } catch (JSONException e) {
                                Log.e("OrderTrackingActivity", "Error parsing JSON: " + e.getMessage());
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("OrderTrackingActivity", "Error fetching data: " + error.getMessage());
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    // New method to fetch product name
    private void fetchProductName(String productId, Item item) {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Product/" + productId; // Adjust the URL to your product API

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String productName = response.getString("name");
                            item.setProductName(productName); // Set the product name in the item
                            orderAdapter.notifyDataSetChanged(); // Notify adapter to refresh the view
                        } catch (JSONException e) {
                            Log.e("OrderTrackingActivity", "Error fetching product name: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("OrderTrackingActivity", "Error fetching product data: " + error.getMessage());
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void setupBottomNavigation(String userId, String token, String username, String email, String[] role) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.button_home) {
                openActivity(MainActivity.class, userId, token, username, email, role);
            } else if (id == R.id.button_products) {
                openActivity(ProductBrowsingActivity.class, userId, token, username, email, role);
                return true;
            } else if (id == R.id.button_all_vendors) {
                openActivity(ViewAllVendors.class, userId, token, username, email, role);
                return true;
            } else if (id == R.id.button_order_history) {
                return true;
            } else if (id == R.id.button_profile) {
                openActivity(UserProfileActivity.class, userId, token, username, email, role);
                return true;
            }
            return false;
        });
    }

    // New openActivity method
    private void openActivity(Class<?> cls, String userId, String token, String username, String email, String[] role) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("TOKEN", token);
        intent.putExtra("USERNAME", username);
        intent.putExtra("EMAIL", email);
        intent.putExtra("ROLE", role); // Pass user role to the next activity
        startActivity(intent);
    }
}