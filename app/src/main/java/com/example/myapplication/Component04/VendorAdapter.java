package com.example.myapplication.Component04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private Context context;
    private List<Vendor> vendorList;
    private String userId; // Declare userId here
    private String token; // Declare token here

    // Update the constructor to accept token
    public VendorAdapter(Context context, List<Vendor> vendorList, String userId, String token) {
        this.context = context;
        this.vendorList = vendorList;
        this.userId = userId; // Initialize userId
        this.token = token; // Initialize token
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vendor_item, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        holder.vendorName.setText(vendor.getName());
        holder.vendorProduct.setText(vendor.getProduct());
        holder.vendorDescription.setText(vendor.getDescription()); 
        holder.vendorRating.setRating((float) vendor.getAverageRating());

        StringBuilder commentsText = new StringBuilder();
        for (Comment comment : vendor.getComments()) { 
            commentsText.append(comment.getCommentText()).append("\n");
        }
        holder.vendorComments.setText(commentsText.toString());

        // Set click listener to open AddCommentDialogFragment
        holder.itemView.setOnClickListener(v -> {
            // Pass userId and token to the dialog
            AddCommentDialogFragment dialog = AddCommentDialogFragment.newInstance(vendor.getId(), userId, token); // Pass token here
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "AddCommentDialog");
        });
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView vendorName;
        TextView vendorProduct;
        TextView vendorDescription;
        TextView vendorComments;
        RatingBar vendorRating;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            vendorName = itemView.findViewById(R.id.vendorName);
            vendorProduct = itemView.findViewById(R.id.vendorProduct);
            vendorDescription = itemView.findViewById(R.id.vendorDescription);
            vendorComments = itemView.findViewById(R.id.vendorComments);
            vendorRating = itemView.findViewById(R.id.vendorRating);
        }
    }
}