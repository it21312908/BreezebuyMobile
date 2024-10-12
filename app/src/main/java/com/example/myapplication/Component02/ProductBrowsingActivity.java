package com.example.myapplication.Component02;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.Component01.UserProfileActivity;
import com.example.myapplication.Component03.OrderTrackingActivity;
import com.example.myapplication.Component04.ViewAllVendors;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductBrowsingActivity extends AppCompatActivity implements ProductAdapter.OnProductCheckedChangeListener {
    private ListView listView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList; // List for filtered products
    private TextView totalAmountText;
    private EditText searchEditText; // EditText for search
    private ProgressDialog progressDialog;
    private String userId;
    private String currentEmail;
    private String token;
    private String username;
    private String email;
    private String savedOrderId;

    private static final String EXTRA_USER_ID = "USER_ID";
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_USERNAME = "USERNAME";
    private static final String EXTRA_EMAIL = "EMAIL";
    private static final String EXTRA_ROLES = "ROLES";

    private static final String PREFS_NAME = "OrderPrefs"; // SharedPreferences file name
    private static final String KEY_ORDER_ID = "orderId"; // Key for order ID
    private static final String KEY_ORDER_DETAILS = "orderDetails"; // Key for order details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_browsing);

        // Initialize views
        listView = findViewById(R.id.list_view);
        totalAmountText = findViewById(R.id.total_amount_text);
        Button addToCartButton = findViewById(R.id.addToCart_button);
        Button viewCartButton = findViewById(R.id.viewCart_button); // Button to view cart
        searchEditText = findViewById(R.id.search_edit_text); // Initialize search EditText

        // Retrieve Intent extras
        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);
        token = intent.getStringExtra(EXTRA_TOKEN);
        String[] roles = intent.getStringArrayExtra(EXTRA_ROLES);
        username = intent.getStringExtra(EXTRA_USERNAME);
        email = intent.getStringExtra(EXTRA_EMAIL);

        // Initialize product list and adapter
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>(); // Initialize filtered list
        productAdapter = new ProductAdapter(filteredProductList, this, this);
        listView.setAdapter(productAdapter);

        // Fetch products from API
        fetchProducts();
        updateTotalAmount();

        // Add to cart button click listener
        addToCartButton.setOnClickListener(v -> saveOrder());

        // View cart button click listener
        viewCartButton.setOnClickListener(v -> openCart());

        // Set up search functionality
        setupSearchFunctionality();

        // Set up bottom navigation
        setupBottomNavigation(userId, token, username, email, roles);
    }

    private void openCart() {
        Intent intent = new Intent(ProductBrowsingActivity.this, CartActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text change
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text change
            }
        });
    }

    private void filterProducts(String query) {
        filteredProductList.clear();
        if (query.isEmpty()) {
            filteredProductList.addAll(productList); // Show all products if query is empty
        } else {
            for (Product product : productList) {
                String productName = product.getName().toLowerCase();
                String categoryName = product.getCategoryName().toLowerCase();
                // Check if product name or category name contains the query
                if (productName.contains(query.toLowerCase()) || categoryName.contains(query.toLowerCase())) {
                    filteredProductList.add(product); // Add matched product to filtered list
                }
            }
        }
        productAdapter.notifyDataSetChanged(); // Notify adapter to refresh the list
    }

    private void fetchProducts() {
        String url = "https://pasindu99-001-site1.etempurl.com/api/product";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading products...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        productList.clear(); // Clear existing items
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Product product = new Product(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("description"),
                                        jsonObject.getDouble("price"),
                                        jsonObject.getString("categoryId"),
                                        ""); // Initialize with empty category name

                                productList.add(product); // Add product to the full list

                                // Fetch category name for the product
                                fetchCategoryName(product); // Pass product to fetchCategoryName

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        filteredProductList.addAll(productList); // Initialize filtered list
                        productAdapter.notifyDataSetChanged();
                        progressDialog.dismiss(); // Dismiss progress dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ProductBrowsing", "Error: " + error.getMessage());
                        progressDialog.dismiss(); // Dismiss progress dialog
                        Toast.makeText(ProductBrowsingActivity.this, "Failed to load products. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void fetchCategoryName(final Product product) {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Category/" + product.getCategoryId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Use optString to avoid JSONException
                        String categoryName = response.optString("name", "Unknown Category");
                        Log.d("CategoryName", "Fetched category name: " + categoryName);

                        // Set the category name for the product
                        product.setCategoryName(categoryName);

                        // Notify adapter that the data set has changed
                        productAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CategoryFetchError", "Error: " + error.getMessage());
                        Toast.makeText(ProductBrowsingActivity.this, "Failed to fetch category name.", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void saveOrder() {
        // Create JSON object for the order
        JSONObject order = new JSONObject();
        try {
            // Generate an order number
            order.put("orderNumber", "ORD" + System.currentTimeMillis());

            // Create the items array
            JSONArray itemsArray = new JSONArray();
            for (Product product : filteredProductList) {
                if (product.isSelected()) {
                    JSONObject item = new JSONObject();
                    item.put("productId", product.getId());
                    item.put("quantity", product.getQuantity()); // Use the actual quantity
                    itemsArray.put(item);
                }
            }
            order.put("items", itemsArray);

            // Send POST request to save the order
            String url = "https://pasindu99-001-site1.etempurl.com/api/Order";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, order,
                    response -> {
                        try {
                            savedOrderId = response.getString("id");
                            Log.d("OrderSaved", "Order saved with ID: " + savedOrderId);
                            Toast.makeText(ProductBrowsingActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            cacheOrderDetails(savedOrderId, order.toString()); // Cache order details

                            // Navigate to the cart after successfully saving the order
                            openCart();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("OrderSaveError", "Error: " + error.getMessage());
                        Toast.makeText(ProductBrowsingActivity.this, "Failed to place order.", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ProductBrowsingActivity.this, "Failed to create order JSON.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cacheOrderDetails(String orderId, String orderDetails) {
        // Save order details in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ORDER_ID, orderId);
        editor.putString(KEY_ORDER_DETAILS, orderDetails);
        editor.apply();
    }

    private void updateTotalAmount() {
        double totalAmount = 0.0;
        for (Product product : filteredProductList) {
            if (product.isSelected()) {
                totalAmount += product.getPrice() * product.getQuantity(); // Calculate total based on quantity
            }
        }
        totalAmountText.setText("Total Amount: $" + totalAmount);
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
        Intent intent = new Intent(ProductBrowsingActivity.this, activityClass);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ROLES, roles);
        startActivity(intent);
    }

//    private void updateTotalAmount() {
//        double totalAmount = 0;
//        for (Product product : filteredProductList) {
//            if (product.isSelected()) {
//                totalAmount += product.getPrice() * product.getQuantity(); // Calculate total price based on selected products
//            }
//        }
//        totalAmountText.setText("Total Amount: $" + String.format("%.2f", totalAmount)); // Update total amount text view
//    }

    @Override
    public void onProductCheckedChanged() {
        updateTotalAmount(); // Recalculate total whenever product selection changes
    }
}