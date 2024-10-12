package com.example.myapplication.Component01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Component02.ProductBrowsingActivity;
import com.example.myapplication.Component03.OrderTrackingActivity;
import com.example.myapplication.Component04.ViewAllVendors;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewId, textViewUsername, textViewEmail, textViewStatus;
    private Button buttonDeactivate, buttonUpdateProfile, buttonLogout; // Added logout button
    private String userId; // Store user ID for API call
    private String currentEmail; // Store current email for the request
    private String token; // Store token for authentication
    private static final String EXTRA_USER_ID = "USER_ID";
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_USERNAME = "USERNAME";
    private static final String EXTRA_EMAIL = "EMAIL";
    private static final String EXTRA_ROLES = "ROLES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
//        textViewId = findViewById(R.id.textViewId);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonDeactivate = findViewById(R.id.buttonDeactivate);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        buttonLogout = findViewById(R.id.buttonLogout); // Initialize logout button

        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        token = intent.getStringExtra("TOKEN");
        String[] roles = intent.getStringArrayExtra("ROLES");
        String username = intent.getStringExtra("USERNAME");
        currentEmail = intent.getStringExtra("EMAIL");

        // Display the user data
        if (userId != null) {
//            textViewId.setText("User ID: " + userId);
        }
        if (username != null) {
            textViewUsername.setText("Username: " + username);
        }
        if (currentEmail != null) {
            textViewEmail.setText("Email: " + currentEmail);
        }

        // Set up button click listeners
        buttonDeactivate.setOnClickListener(v -> deactivateAccount());
        buttonUpdateProfile.setOnClickListener(v -> showUpdateProfileDialog());
        buttonLogout.setOnClickListener(v -> logout()); // Set listener for logout

        // Setup Bottom Navigation
        setupBottomNavigation(userId, token, username, currentEmail, roles); // Pass token to the bottom navigation
        fetchUserData();
    }

    private void fetchUserData() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://pasindu99-001-site1.etempurl.com/Auth/me";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FetchUserData", "Request failed", e);
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String userId = jsonObject.getString("id");
                        String username = jsonObject.getString("username");
                        String email = jsonObject.getString("email");

                        runOnUiThread(() -> {
//                            textViewId.setText("User ID: " + userId);
                            textViewUsername.setText("Username: " + username);
                            textViewEmail.setText("Email: " + email);
                        });
                    } catch (Exception e) {
                        Log.e("FetchUserData", "JSON parsing error", e);
                    }
                } else {
                    Log.e("FetchUserData", "Response Code: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(UserProfileActivity.this, "Failed to fetch user data: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void deactivateAccount() {
        OkHttpClient client = new OkHttpClient();

        // Send PUT request to deactivate account
        String url = "https://pasindu99-001-site1.etempurl.com/Auth/deactivateAccount";

        // Create the request without a body
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(null, new byte[0])) // Use empty body for PUT request
                .addHeader("Authorization", "Bearer " + token) // Add the authorization token
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log the exception for debugging
                Log.e("DeactivateAccount", "Request failed", e);
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "Failed to deactivate account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check response details
                Log.d("DeactivateAccount", "Response Code: " + response.code());
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Correct context reference for Intent
                        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(UserProfileActivity.this, "Failed to deactivate account: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void showUpdateProfileDialog() {
        // Inflate dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_update_profile, null);

        // Initialize dialog views
        EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextCurrentPassword = dialogView.findViewById(R.id.editTextCurrentPassword);
        EditText editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmNewPassword = dialogView.findViewById(R.id.editTextConfirmNewPassword);
        Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);

        // Pre-fill the email field with the current email
        editTextEmail.setText(currentEmail);

        // Safely set the current username
        String[] usernameParts = textViewUsername.getText().toString().split(": ");
        if (usernameParts.length > 1) {
            editTextUsername.setText(usernameParts[1]); // Set current username
        } else {
            editTextUsername.setText(""); // Fallback if username is not available
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setView(dialogView)
                .create();

        buttonUpdate.setOnClickListener(v -> {
            String newUsername = editTextUsername.getText().toString().trim();
            String newEmail = editTextEmail.getText().toString().trim();
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

            // Validate input
            if (newPassword.equals(confirmNewPassword)) {
                // Call the updateProfile method
                updateProfile(newUsername, newEmail, currentPassword, newPassword, dialog);
            } else {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void updateProfile(String username, String email, String currentPassword, String newPassword, AlertDialog dialog) {
        OkHttpClient client = new OkHttpClient();

        // Create JSON object for request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("email", email); // Use the new email from input
            jsonObject.put("CurrentPassword", currentPassword);
            jsonObject.put("NewPassword", newPassword);
            jsonObject.put("ConfirmNewPassword", newPassword); // Confirming the new password
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send PUT request to update the profile
        String url = "https://pasindu99-001-site1.etempurl.com/Auth/update"; // Modify URL to include user ID
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + token) // Add the authorization token
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UpdateProfile", "Request failed", e);
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Dismiss dialog on failure
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Update displayed user data
                        textViewUsername.setText("Username: " + username);
                        textViewEmail.setText("Email: " + email); // Update displayed email
                        currentEmail = email; // Store updated email
                        dialog.dismiss(); // Dismiss dialog on success
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(UserProfileActivity.this, "Failed to update profile: " + response.code(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Dismiss dialog on failure
                    });
                }
            }
        });
    }

    private void logout() {
        // Perform logout actions
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation(String userId, String token, String username, String email, String[] roles) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.button_home) {
                openActivity(MainActivity.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_products) {
                openActivity(ProductBrowsingActivity.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_all_vendors) {
                openActivity(ViewAllVendors.class, userId, token, username, email, roles);
                return true;
            } else if (id == R.id.button_order_history) {
                openActivity(OrderTrackingActivity.class, userId, token, username, email, roles); // Adjust to your OrderHistoryActivity
                return true;
            } else if (id == R.id.button_profile) {
                return true;
            }
            return false;
        });
    }

    private void openActivity(Class<?> activityClass, String userId, String token, String username, String email, String[] roles) {
        Intent intent = new Intent(UserProfileActivity.this, activityClass);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ROLES, roles);
        startActivity(intent);
    }
}