package com.example.myapplication.Component04;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

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

public class ViewAllVendors extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VendorAdapter vendorAdapter;
    private List<Vendor> vendorList = new ArrayList<>(); // List of all vendors
    private List<Vendor> filteredVendorList = new ArrayList<>(); // List for filtered vendors
    private SearchView searchView; // SearchView for filtering vendors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_vendors);

        initializeViews(); // Initialize views
        setupRecyclerView(); // Set up RecyclerView
        loadVendorData(); // Load vendor data from JSON file
        setupSearchFunctionality(); // Set up search functionality
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewVendors);
        searchView = findViewById(R.id.searchView);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vendorAdapter = new VendorAdapter(filteredVendorList, this::showCommentDialog);
        recyclerView.setAdapter(vendorAdapter);
    }

    private void loadVendorData() {
        try {
            InputStream inputStream = getAssets().open("vendor.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            // Read JSON data from the file
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse JSON data
            parseVendorJson(jsonBuilder.toString());
            reader.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace(); // Handle error loading data or JSON parsing error
        }

        // Initially, show all vendors
        filteredVendorList.addAll(vendorList);
        vendorAdapter.notifyDataSetChanged(); // Refresh the view
    }

    private void parseVendorJson(String jsonData) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Vendor vendor = new Vendor(
                    jsonObject.optString("id", ""),
                    jsonObject.optString("vendorName", ""),
                    jsonObject.optString("category", ""),
                    jsonObject.optString("description", ""),
                    jsonObject.optDouble("averageRating", 0.0) // Updated to averageRating
            );

            // Parse and add comments
            JSONArray commentsArray = jsonObject.optJSONArray("comments");
            if (commentsArray != null) {
                for (int j = 0; j < commentsArray.length(); j++) {
                    JSONObject commentObject = commentsArray.getJSONObject(j);
                    Comment comment = new Comment(
                            commentObject.optString("userId", ""),
                            commentObject.optString("comment", ""),
                            commentObject.optInt("rating", 0),
                            commentObject.optString("date", "")
                    );
                    vendor.addComment(comment); // Add comment to vendor
                }
            }

            vendorList.add(vendor); // Add vendor to the list
        }
    }

    private void setupSearchFunctionality() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No action on submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVendors(newText); // Filter vendors based on the search query
                return true;
            }
        });
    }

    private void filterVendors(String query) {
        filteredVendorList.clear(); // Clear the filtered list
        if (query.isEmpty()) {
            filteredVendorList.addAll(vendorList); // If query is empty, show all vendors
        } else {
            // Filter vendors by name
            for (Vendor vendor : vendorList) {
                if (vendor.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredVendorList.add(vendor);
                }
            }
        }
        vendorAdapter.notifyDataSetChanged(); // Refresh the view
    }

    public void showCommentDialog(Vendor vendor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_comment, null);
        builder.setView(dialogView);

        LinearLayout starLayout = dialogView.findViewById(R.id.starLayout);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);

        // Set star click listeners
        setupStarClickListeners(starLayout, vendor);

        builder.setTitle("Add Comment and Rating")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String commentText = commentInput.getText().toString();
                    Comment newComment = new Comment("userId", commentText, (int) vendor.getAverageRating(), "date"); // Sample data for new comment
                    vendor.addComment(newComment);
                    vendorAdapter.notifyDataSetChanged(); // Refresh the adapter
                    dialog.dismiss(); // Dismiss dialog
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void setupStarClickListeners(LinearLayout starLayout, Vendor vendor) {
        for (int i = 1; i <= 5; i++) {
            int starId = getResources().getIdentifier("star" + i, "id", getPackageName());
            ImageView star = starLayout.findViewById(starId);
            int finalI = i; // Final variable for use in the listener

            star.setOnClickListener(v -> {
                vendor.setAverageRating(finalI); // Updated to averageRating
                updateStarSelection(starLayout, finalI); // Update star selection
            });
        }
        updateStarSelection(starLayout, (int) vendor.getAverageRating()); // Initialize star selection
    }

    // Function to update star selection based on rating
    private void updateStarSelection(LinearLayout starLayout, int rating) {
        for (int i = 0; i < starLayout.getChildCount(); i++) {
            ImageView star = (ImageView) starLayout.getChildAt(i);
            if (i < rating) {
                star.setImageResource(R.drawable.star_filled); // Filled star
            } else {
                star.setImageResource(R.drawable.star_empty); // Empty star
            }
        }
    }
}