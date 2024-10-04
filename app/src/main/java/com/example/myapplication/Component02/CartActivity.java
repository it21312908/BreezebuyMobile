package com.example.myapplication.Component02;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> purchasedProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(purchasedProducts, this);
        recyclerView.setAdapter(productAdapter);

        // Get purchased products from intent
        List<Product> products = (List<Product>) getIntent().getSerializableExtra("purchasedProducts");
        if (products != null) {
            purchasedProducts.addAll(products);
        }

        productAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the view
    }
}