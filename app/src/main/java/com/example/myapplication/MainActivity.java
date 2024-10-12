package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Component01.UserProfileActivity;
import com.example.myapplication.Component02.CartActivity;
import com.example.myapplication.Component02.ProductBrowsingActivity;
import com.example.myapplication.Component03.OrderTrackingActivity;
import com.example.myapplication.Component04.ViewAllVendors;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private String textViewWelcomeUser;
    private String textViewUserId;

    // Intent Extra Keys
    private static final String EXTRA_USER_ID = "USER_ID";
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_USERNAME = "USERNAME";
    private static final String EXTRA_EMAIL = "EMAIL";
    private static final String EXTRA_ROLES = "ROLES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI Components
        initializeViews();

        // Retrieve user info from Intent
        Intent intent = getIntent();
        String token = intent.getStringExtra(EXTRA_TOKEN);
        String username = intent.getStringExtra(EXTRA_USERNAME);
        String userId = intent.getStringExtra(EXTRA_USER_ID);
        String email = intent.getStringExtra(EXTRA_EMAIL);
        String[] roles = intent.getStringArrayExtra(EXTRA_ROLES); // Get roles array

        // Setup BottomNavigationView
        setupBottomNavigation(userId, token, username, email, roles);
    }

    private void initializeViews() {
        textViewWelcomeUser = "1";
        textViewUserId = "1";
    }

    private void setupBottomNavigation(String userId, String token, String username, String email, String[] roles) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Remove the profile item for customers
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.button_home) {
                openActivity(MainActivity.class, userId, token, username, email, roles); // Pass roles here
            } else if (id == R.id.button_products) {
                openActivity(ProductBrowsingActivity.class, userId, token, username, email, roles); // Pass roles here
                return true;
            } else if (id == R.id.button_all_vendors) {
                openActivity(ViewAllVendors.class, userId, token, username, email, roles); // Pass roles here
                return true;
            } else if (id == R.id.button_order_history) {
                openActivity(OrderTrackingActivity.class, userId, token, username, email, roles); // Pass roles here
                return true;
            } else if (id == R.id.button_profile) {
                openActivity(UserProfileActivity.class, userId, token, username, email, roles);
                return true;
            }
            return false;
        });
    }

    // Check if the user has the Customer role
    private boolean isCustomer(String[] roles) {
        if (roles != null) {
            for (String role : roles) {
                if ("Customer".equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void openProfileActivity(String userId, String username, String email, String token) {
        Intent profileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        profileIntent.putExtra(EXTRA_USER_ID, userId);
        profileIntent.putExtra(EXTRA_USERNAME, username);
        profileIntent.putExtra(EXTRA_EMAIL, email);
        profileIntent.putExtra(EXTRA_TOKEN, token);
        startActivity(profileIntent);
    }

    // Corrected method signature to include username parameter
    private void openActivity(Class<?> activityClass, String userId, String token, String username, String email, String[] roles) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ROLES, roles);
        startActivity(intent);
    }
}