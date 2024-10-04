package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Component01.AccountCreationActivity;
import com.example.myapplication.Component01.LoginActivity;
import com.example.myapplication.Component01.UserProfileActivity;
import com.example.myapplication.Component02.ProductBrowsingActivity;
import com.example.myapplication.Component03.OrderHistoryActivity;
import com.example.myapplication.Component04.ViewAllVendors;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogin, buttonCreateAccount, buttonAllVendors, buttonProfile, buttonCart, buttonProducts, buttonOrderHistory;
    private TextView textViewLoginDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons and TextView
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        buttonAllVendors = findViewById(R.id.buttonAllVendors);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonCart = findViewById(R.id.buttonCart);
        buttonProducts = findViewById(R.id.buttonProducts);
        textViewLoginDetails = findViewById(R.id.textViewLoginDetails);
        buttonOrderHistory = findViewById(R.id.buttonOrderHistory);

        // Retrieve login details from Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String status = intent.getStringExtra("STATUS");

        // Display the username and status
        if (username != null && status != null) {
            textViewLoginDetails.setText("Logged in as: " + username + " (Status: " + status + ")");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up button click listeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountCreationActivity.class));
            }
        });

        buttonAllVendors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewAllVendors.class));
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProductBrowsingActivity.class));
            }
        });

        buttonProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProductBrowsingActivity.class));
            }
        });

        buttonOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
            }
        });
    }
}