package com.example.myapplication.Component04;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

import java.io.OutputStream;
import java.net.HttpURLConnection; 
import java.net.URL;

public class AddCommentDialogFragment extends DialogFragment {

    private EditText commentEditText;
    private RatingBar ratingBar;
    private Button submitButton;  
    private String vendorId;
    private String userId; // Declare userId
    private String token; // Declare token

    public static AddCommentDialogFragment newInstance(String vendorId, String userId, String token) {
        AddCommentDialogFragment fragment = new AddCommentDialogFragment();
        Bundle args = new Bundle();
        args.putString("vendorId", vendorId);
        args.putString("userId", userId); // Pass userId here
        args.putString("token", token); // Pass token here
        fragment.setArguments(args); // Set the arguments
        return fragment;
    }

    // Listener interface for notifying the host about comment submission
    public interface OnCommentSubmittedListener {
        void onCommentSubmitted(); // Define the callback method
    }

    private OnCommentSubmittedListener listener;

    public void setOnCommentSubmittedListener(OnCommentSubmittedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_comment, container, false);

        commentEditText = view.findViewById(R.id.commentEditText); 
        ratingBar = view.findViewById(R.id.ratingBar);
        submitButton = view.findViewById(R.id.submitButton);

        // Get vendorId, userId, and token from arguments
        vendorId = getArguments().getString("vendorId");
        userId = getArguments().getString("userId"); // Retrieve userId
        token = getArguments().getString("token"); // Retrieve token

        submitButton.setOnClickListener(v -> submitComment());

        return view;
    }

    private void submitComment() {
        String commentText = commentEditText.getText().toString();
        int rank = (int) ratingBar.getRating(); // Assuming rating is between 1-5

        // Validate inputs
        if (commentText.isEmpty() || rank < 1) {
            commentEditText.setError("Comment and rating are required.");
            return;
        }

        sendComment(vendorId, commentText, rank);
    }

    private void sendComment(String vendorId, String commentText, int rank) {
        new Thread(() -> {
            try {
                String apiUrl = "https://pasindu99-001-site1.etempurl.com/api/Vendor/" + vendorId + "/feedback";

                // Create the JSON object to send
                String jsonInputString = String.format(
                        "{\"customerId\": \"%s\", \"rank\": %d, \"commentText\": \"%s\", \"isCommentEditable\": true}",
                        userId, // Use userId here
                        rank, commentText
                );

                // Open a connection to the API
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token); // Use the passed token
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Send the request
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Handle successful response
                    requireActivity().runOnUiThread(() -> {
                        dismiss(); // Close the dialog on the main thread
                        if (listener != null) {
                            listener.onCommentSubmitted(); // Notify the host activity/fragment
                        }
                    });
                } else {
                    // Handle error response
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
            }
        }).start();
    }
}