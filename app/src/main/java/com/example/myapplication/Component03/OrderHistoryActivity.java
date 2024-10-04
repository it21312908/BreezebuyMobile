package com.example.myapplication.Component03;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OrderHistoryActivity extends AppCompatActivity {

    private LinearLayout orderListView;
    private static final String TARGET_USER_ID = "12345"; // Set the target UserId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        orderListView = findViewById(R.id.orderListView); // Assuming it's a LinearLayout in the activity layout

        // Load order data from cart.json
        loadOrdersFromJSON(this);
    }

    private void loadOrdersFromJSON(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("cart.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            String jsonData = stringBuilder.toString();
            JSONArray jsonArray = new JSONArray(jsonData);

            // Iterate through each order in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject order = jsonArray.getJSONObject(i);
                String userId = order.getString("UserId");

                // Check if the UserId matches the target UserId
                if (userId.equals(TARGET_USER_ID)) {
                    // Create a new view for the order
                    View orderView = LayoutInflater.from(this).inflate(R.layout.list_item_order, null);

                    // Set User ID
                    TextView userIdTextView = orderView.findViewById(R.id.userIdTextView);
                    userIdTextView.setText(userId);

                    LinearLayout itemsContainer = orderView.findViewById(R.id.itemsContainer);
                    TextView totalPriceTextView = orderView.findViewById(R.id.totalPriceTextView);

                    JSONArray items = order.getJSONArray("Items");
                    double totalPrice = 0; // Initialize total price for this order

                    // Iterate through items and create TextViews for each
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject item = items.getJSONObject(j);
                        String productName = item.getString("ProductName");
                        String price = item.getString("Price");
                        int quantity = item.getInt("Quantity");

                        // Calculate total price
                        totalPrice += Double.parseDouble(price) * quantity;

                        // Create a TextView for each item
                        TextView itemTextView = new TextView(this);
                        itemTextView.setText(String.format("Product: %s | Price: %s | Quantity: %d", productName, price, quantity));
                        itemTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        itemsContainer.addView(itemTextView);
                    }

                    // Set total price
                    totalPriceTextView.setText(String.format("Total Price: %s", totalPrice));

                    // Add the order view to the parent layout
                    orderListView.addView(orderView);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e("OrderHistoryActivity", "Error loading JSON data", e);
        }
    }
}