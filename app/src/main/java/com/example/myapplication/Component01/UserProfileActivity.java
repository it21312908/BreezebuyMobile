package com.example.myapplication.Component01;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewId, textViewUsername, textViewEmail, textViewStatus;
    private Button buttonDeactivate, buttonUpdateProfile;
    private ImageView profileImage;
    String _id = "66fab746f32ba70f32df1dd0"; // Replace this with the desired user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        textViewId = findViewById(R.id.textViewId); // Add this line
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonDeactivate = findViewById(R.id.buttonDeactivate);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        profileImage = findViewById(R.id.profileImage);

        // Load user data from JSON
        loadUserDataFromJson();

        // Handle Update Profile button click
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateProfileDialog();
            }
        });

        // Handle Deactivate button click
        buttonDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeactivateConfirmation();
            }
        });
    }

    // Load user data from user.json
    private void loadUserDataFromJson() {
        try {
            // Open the JSON file from assets
            InputStream is = getAssets().open("user.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string
            String json = new String(buffer, "UTF-8");

            // Parse the JSON string to get the user array
            JSONArray jsonArray = new JSONArray(json);

            // Loop through the array and find the user with the specific _id
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userId = jsonObject.getString("_id");

                // Check if this is the user we want
                if (userId.equals(_id)) {
                    String username = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    String status = jsonObject.getString("status");

                    // Update the UI with the loaded user data
                    textViewId.setText("User ID: " + userId); // Ensure this TextView is added in the layout
                    textViewUsername.setText("Username: " + username);
                    textViewEmail.setText("Email: " + email);
                    textViewStatus.setText("Status: " + status);
                    break; // Exit loop once the user is found
                }
            }

        } catch (IOException e) {
            Log.e("UserProfileActivity", "IOException: " + e.getMessage());
            Toast.makeText(this, "Failed to load user data: IOException", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e("UserProfileActivity", "JSONException: " + e.getMessage());
            Toast.makeText(this, "Failed to load user data: JSONException", Toast.LENGTH_SHORT).show();
        }
    }

    // Show update profile dialog
    private void showUpdateProfileDialog() {
        // Inflate the dialog layout
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_profile, null);
        final TextView editUsername = dialogView.findViewById(R.id.editUsername);
        final TextView editEmail = dialogView.findViewById(R.id.editEmail);

        // Get current values
        String currentUsername = textViewUsername.getText().toString().replace("Username: ", "");
        String currentEmail = textViewEmail.getText().toString().replace("Email: ", "");

        // Set current values in edit texts
        editUsername.setText(currentUsername);
        editEmail.setText(currentEmail);

        new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setView(dialogView)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = editUsername.getText().toString().trim();
                        String newEmail = editEmail.getText().toString().trim();

                        // Save updated data to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", newUsername);
                        editor.putString("email", newEmail);
                        editor.apply();

                        // Update the displayed values
                        textViewUsername.setText("Username: " + newUsername);
                        textViewEmail.setText("Email: " + newEmail);
                        Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Show confirmation dialog for account deactivation
    private void showDeactivateConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform account deactivation logic (e.g., logout, disable account)
                        Toast.makeText(UserProfileActivity.this, "Account deactivated", Toast.LENGTH_SHORT).show();
                        // Go back to login screen after deactivation
                        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}