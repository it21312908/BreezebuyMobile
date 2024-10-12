package com.example.myapplication.Component02;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Component03.OrderTrackingActivity;
import com.example.myapplication.Component04.ViewAllVendors;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ListView cartListView;
    private CartAdapter cartAdapter;
    private List<OrderItem> cartItemList;
    private TextView totalAmountText;

    private String userId;
    private String currentEmail;
    private String token;
    private String username;
    private String email;
    private String[] roles;
    private Button purchaseButton;
    private String orderId;

    private static final String EXTRA_USER_ID = "USER_ID";
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_USERNAME = "USERNAME";
    private static final String EXTRA_EMAIL = "EMAIL";
    private static final String EXTRA_ROLES = "ROLES";
    private static final String PREFS_NAME = "OrderPrefs";
    private static final String KEY_ORDER_ID = "orderId";
    private static final String KEY_ORDER_DETAILS = "orderDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        cartListView = findViewById(R.id.cart_list_view);
        totalAmountText = findViewById(R.id.total_amount_text);
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, this);
        cartListView.setAdapter(cartAdapter);

        // Retrieve the saved order data from shared preferences (optional)
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        orderId = sharedPreferences.getString(KEY_ORDER_ID, "");
        String orderDetails = sharedPreferences.getString(KEY_ORDER_DETAILS, "");

        if (!orderId.isEmpty() && !orderDetails.isEmpty()) {
            // Use the retrieved orderId and orderDetails as needed
            fetchCartItems();  // Example of using the retrieved orderId
            Log.d("CartActivity", "Order ID: " + orderId);
            Log.d("CartActivity", "Order Details: " + orderDetails);
        } else {
            Log.d("CartActivity", "No saved order data found.");
        }




        // Retrieve Intent extras
        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);
        token = intent.getStringExtra(EXTRA_TOKEN);
        String[] roles = intent.getStringArrayExtra(EXTRA_ROLES);
        username = intent.getStringExtra(EXTRA_USERNAME);
        email = intent.getStringExtra(EXTRA_EMAIL);

        // Fetch cart items (if not retrieved from shared preferences)
        if (orderId.isEmpty()) {
            fetchCartItems();
        }

        Button purchaseButton = findViewById(R.id.purchase_button);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseItems(); // Call your purchase method here
                // After the purchase is successful, redirect to ProductBrowsingActivity
                Intent intent = new Intent(CartActivity.this, ProductBrowsingActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                intent.putExtra(EXTRA_TOKEN, token);
                intent.putExtra(EXTRA_USERNAME, username);
                intent.putExtra(EXTRA_EMAIL, email);
                intent.putExtra(EXTRA_ROLES, roles);
                startActivity(intent);
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItemList.size() > 0) {
                    // Remove the first item for demonstration (you can adjust this logic)
                    OrderItem itemToDelete = cartItemList.get(0);
                    deleteItem(itemToDelete);

                    // Redirect to ProductBrowsingActivity after item deletion
                    Intent intent = new Intent(CartActivity.this, ProductBrowsingActivity.class);
                    intent.putExtra(EXTRA_USER_ID, userId);
                    intent.putExtra(EXTRA_TOKEN, token);
                    intent.putExtra(EXTRA_USERNAME, username);
                    intent.putExtra(EXTRA_EMAIL, email);
                    intent.putExtra(EXTRA_ROLES, roles);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "No items to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupBottomNavigation(userId, token, username, email, roles);
    }

    private void calculateTotalAmount() {
        double totalAmount = 0;
        for (OrderItem item : cartItemList) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        totalAmountText.setText("Total: $" + String.format("%.2f", totalAmount));
    }

    private void fetchCartItems() {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Order/" + orderId; // Update this to match your API structure

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        cartItemList.clear(); // Clear previous items

                        // Access the items array from the response
                        JSONArray itemsArray = response.getJSONArray("items");
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject jsonObject = itemsArray.getJSONObject(i);
                            OrderItem item = new OrderItem(
                                    jsonObject.getString("productId"),
                                    jsonObject.getDouble("price"),
                                    jsonObject.getInt("quantity"),
                                    jsonObject.getDouble("totalAmount") // Get totalAmount from response
                            );
                            cartItemList.add(item);
                        }
                        cartAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                        calculateTotalAmount();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CartActivity.this, "Error fetching cart items", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CartActivity", "Error: " + error.getMessage());
                    Toast.makeText(CartActivity.this, "Failed to fetch cart items", Toast.LENGTH_SHORT).show();
                }
        );

        // Adding the request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void deleteItem(OrderItem item) {
        // Construct the API endpoint for deleting an item
        String url = "https://pasindu99-001-site1.etempurl.com/api/Order/" + orderId;

        // Create a DELETE request
        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Remove the item from the cartItemList
                        cartItemList.remove(item);

                        // Notify the adapter to update the ListView
                        cartAdapter.notifyDataSetChanged();

                        // Recalculate the total amount
                        calculateTotalAmount();

                        // Display success message
                        Toast.makeText(CartActivity.this, "Order removed successfully!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Display an error message
                        Toast.makeText(CartActivity.this, "Failed to delete item: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the delete request to the RequestQueue
        VolleySingleton.getInstance(this).addToRequestQueue(deleteRequest);
    }

    private void purchaseItems() {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Order/" + orderId; // Your API endpoint

        // Step 1: Fetch existing order data
        JsonObjectRequest fetchRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Step 2: Modify the status
                            response.put("status", "purchased"); // Update status to "purchase"

                            // Step 3: Send the complete object back to the server
                            JsonObjectRequest updateRequest = new JsonObjectRequest(
                                    Request.Method.PUT,
                                    url,
                                    response,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // Handle the response from the server
                                            Toast.makeText(CartActivity.this, "Order is purchased successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // Handle error
                                            Toast.makeText(CartActivity.this, "Error updating order status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );

                            // Add the update request to the RequestQueue
                            Volley.newRequestQueue(CartActivity.this).add(updateRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(CartActivity.this, "Error fetching order data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the fetch request to the RequestQueue
        Volley.newRequestQueue(this).add(fetchRequest);
    }

    private void setupBottomNavigation(String userId, String token, String username, String email, String[] roles) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.button_home) {
                openActivity(MainActivity.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_products) {
                openActivity(ProductBrowsingActivity.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_all_vendors) {
                openActivity(ViewAllVendors.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_order_history) {
                openActivity(OrderTrackingActivity.class, userId, token, username, email, roles); // Adjust to your OrderHistoryActivity
                return true;
            } else if (id == R.id.button_profile) {
                return true;
            }
            return false;
        });
    }

    private void openActivity(Class<?> activityClass, String userId, String token, String username, String email, String[] roles) {
        Intent intent = new Intent(CartActivity.this, activityClass);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ROLES, roles);
        startActivity(intent);
    }
}