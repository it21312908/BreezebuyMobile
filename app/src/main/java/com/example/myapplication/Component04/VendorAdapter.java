package com.example.myapplication.Component04;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private List<Vendor> vendorList; // List of vendors
    private OnVendorClickListener onVendorClickListener; // Listener for item clicks

    // Interface to handle vendor click events
    public interface OnVendorClickListener {
        void onVendorClick(Vendor vendor);
    }

    // Constructor
    public VendorAdapter(List<Vendor> vendorList, OnVendorClickListener listener) {
        this.vendorList = vendorList;
        this.onVendorClickListener = listener;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the vendor item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_item, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        holder.bind(vendor); // Bind vendor data to the ViewHolder
    }

    @Override
    public int getItemCount() {
        return vendorList.size(); // Return the size of the vendor list
    }

    // ViewHolder class to hold vendor item views
    class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView vendorName; // TextView for vendor name
        TextView vendorDescription; // TextView for vendor description
        TextView vendorRating; // TextView for vendor average rating

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            vendorName = itemView.findViewById(R.id.vendorName);
            vendorDescription = itemView.findViewById(R.id.vendorDescription);
            vendorRating = itemView.findViewById(R.id.vendorRating); // Initialize average rating TextView

            // Set click listener for the entire item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onVendorClickListener.onVendorClick(vendorList.get(position)); // Notify the listener
                }
            });
        }

        // Method to bind vendor data to the TextViews
        public void bind(Vendor vendor) {
            vendorName.setText(vendor.getName()); // Updated to match new Vendor method
            vendorDescription.setText(vendor.getDescription()); // Updated to match new Vendor method
            vendorRating.setText(String.format("%.1f", vendor.getAverageRating())); // Set the average rating formatted to one decimal
        }
    }
}