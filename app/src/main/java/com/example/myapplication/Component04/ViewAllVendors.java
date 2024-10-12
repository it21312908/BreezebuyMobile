package com.example.myapplication.Component04;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Component01.UserProfileActivity;
import com.example.myapplication.Component02.ProductBrowsingActivity;
import com.example.myapplication.Component03.OrderTrackingActivity;
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

public class ViewAllVendors extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VendorAdapter vendorAdapter;
    private List<Vendor> vendorList;
    private ProgressBar progressBar;
    private String userId; // Store user ID for API call
    private String currentEmail; // Store current email for the request
    private String token; // Store token for authentication
    private static final String EXTRA_USER_ID = "USER_ID";
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_USERNAME = "USERNAME";
    private static final String EXTRA_EMAIL = "EMAIL";
    private static final String EXTRA_ROLES = "ROLES";

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchVendorData(); // Fetch vendor data
            handler.postDelayed(this, 5000); // Re-run every 5 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_vendors);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("USER_ID");
        String token = intent.getStringExtra("TOKEN");
        String[] roles = intent.getStringArrayExtra("ROLES");
        String username = intent.getStringExtra("USERNAME");
        String currentEmail = intent.getStringExtra("EMAIL");

        // Initialize RecyclerView, VendorAdapter, and ProgressBar
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar); // Initialize the ProgressBar
        vendorList = new ArrayList<>();
        vendorAdapter = new VendorAdapter(this, vendorList, userId, token);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(vendorAdapter);

        // Fetch vendor data
        fetchVendorData();

        // Set up bottom navigation
        setupBottomNavigation(userId, token, username, currentEmail, roles);
    }

    private void fetchVendorData() {
        String url = "https://pasindu99-001-site1.etempurl.com/api/Vendor";

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Hide loading indicator
                        progressBar.setVisibility(View.GONE);
                        parseVendorData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hide loading indicator
                        progressBar.setVisibility(View.GONE);
                        Log.e("ViewAllVendors", "Error fetching vendor data: " + error.getMessage());
                        Toast.makeText(ViewAllVendors.this, "Failed to fetch vendor data. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // Set the token in headers
                return headers; // Return the headers
            }
        };

        // Add request to the Volley request queue
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void parseVendorData(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject vendorObject = response.getJSONObject(i);
                String id = vendorObject.getString("id");
                String name = vendorObject.getString("name");
                String product = vendorObject.getString("product");
                String description = vendorObject.getString("description");
                double averageRating = vendorObject.getDouble("averageRating");

                // Parse comments
                List<Comment> comments = new ArrayList<>();
                JSONArray commentsArray = vendorObject.getJSONArray("comments");
                for (int j = 0; j < commentsArray.length(); j++) {
                    JSONObject commentObject = commentsArray.getJSONObject(j);
                    String commentId = commentObject.getString("id");
                    String customerId = commentObject.getString("customerId");
                    int rank = commentObject.getInt("rank");
                    String commentText = commentObject.getString("commentText");
                    boolean isEditable = commentObject.getBoolean("isCommentEditable");

                    comments.add(new Comment(commentId, customerId, rank, commentText, isEditable));
                }

                // Add vendor to list
                vendorList.add(new Vendor(id, name, product, description, averageRating, comments));
            }
            vendorAdapter.notifyDataSetChanged(); // Notify adapter of data change
        } catch (JSONException e) {
            Log.e("ViewAllVendors", "JSON parsing error: " + e.getMessage());
            Toast.makeText(ViewAllVendors.this, "Error parsing vendor data. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAddCommentDialog(String vendorId) {
        AddCommentDialogFragment dialogFragment = AddCommentDialogFragment.newInstance(vendorId, userId, token);
        dialogFragment.show(getSupportFragmentManager(), "AddComment");
    }

    private void setupBottomNavigation(String userId, String token, String username, String email, String[] roles) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.button_home) {
                openActivity(MainActivity.class, userId, token, username, email, roles);
            } else if (id == R.id.button_products) {
                openActivity(ProductBrowsingActivity.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_all_vendors) {
                return true;
            } else if (id == R.id.button_order_history) {
                openActivity(OrderTrackingActivity.class, userId, token, username, email, roles); // Adjust to your OrderHistoryActivity
                return true;
            } else if (id == R.id.button_profile) {
                openActivity(UserProfileActivity.class, userId, token, username, email, roles);
                return true;
            }
            return false;
        });
    }

    private void openActivity(Class<?> cls, String userId, String token, String username, String email, String[] roles) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("TOKEN", token);
        intent.putExtra("USERNAME", username);
        intent.putExtra("EMAIL", email);
        intent.putExtra("ROLE", roles);
        startActivity(intent);
    }
}