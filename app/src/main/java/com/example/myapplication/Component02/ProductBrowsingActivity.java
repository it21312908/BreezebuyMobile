package com.example.myapplication.Component02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProductBrowsingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> purchasedProducts = new ArrayList<>(); // List to hold purchased products
    private Button viewCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_browsing);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        viewCartButton = findViewById(R.id.viewCartButton);
        viewCartButton.setOnClickListener(v -> openCart());

        // Load product data from JSON
        loadProductData();
    }

    private void loadProductData() {
        try {
            InputStream inputStream = getAssets().open("product.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse JSON
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Map the JSON data to Product object fields
                String id = jsonObject.getString("_id");
                String name = jsonObject.getString("Name");
                String description = jsonObject.getString("Description");
                double price = jsonObject.getDouble("Price");
                boolean isActive = jsonObject.getBoolean("IsActive");
                int quantity = jsonObject.getInt("Quantity");
                String categoryId = jsonObject.getString("CategoryId");

                // Add product to the list if it's active
                if (isActive) {
                    productList.add(new Product(id, name, description, price, quantity, categoryId));
                }
            }

            reader.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // Handle error loading data
        }

        productAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    public void markAsPurchased(Product product) {
        purchasedProducts.add(product); // Add the product to the purchased list
        product.setPurchased(true); // Mark the product as purchased
        productAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    private void openCart() {
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("purchasedProducts", new ArrayList<>(purchasedProducts)); // Pass the purchased products to the CartActivity
        startActivity(intent);
    }
}