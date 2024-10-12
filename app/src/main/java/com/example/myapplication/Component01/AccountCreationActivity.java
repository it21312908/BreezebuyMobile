package com.example.myapplication.Component01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccountCreationActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonCreateAccount;
    private TextView buttonBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupCreateAccountButton();
        setupBackToLoginButton();
    }

    private void initializeViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);
    }

    private void setupCreateAccountButton() {
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void setupBackToLoginButton() {
        buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLoginActivity();
            }
        });
    }

    private void createAccount() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required");
        } else if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
        } else if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
        } else {
            // Call the API to create an account
            new CreateAccountTask(username, email, password).execute();
        }
    }

    // AsyncTask to handle network operation on a background thread
    private class CreateAccountTask extends AsyncTask<Void, Void, String> {

        private String username;
        private String email;
        private String password;

        public CreateAccountTask(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://pasindu99-001-site1.etempurl.com/Auth/register/customerMobile");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Create JSON object to send to the server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("email", email);

                // Write the JSON object to the request body
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = urlConnection.getResponseCode();
                Log.d("CreateAccountTask", "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d("CreateAccountTask", "Response: " + response.toString());

                    // Check if the response is not empty
                    if (response.length() == 0) {
                        return "Account created successfully";
                    }

                    // Convert response to JSON and check for success
                    JSONObject responseObject = new JSONObject(response.toString());
                    String status = responseObject.optString("status", null); // Check if a status field exists

                    if ("success".equals(status)) {
                        return "Account created successfully";
                    } else {
                        return "Failed to create account: " + responseObject.optString("message", "Unknown error");
                    }
                } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    // Handle error response for bad request
                    BufferedReader errorStream = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorStream.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorStream.close();
                    Log.d("CreateAccountTask", "Error response: " + errorResponse.toString());
                    return "Failed to create account. Error: " + errorResponse.toString();
                } else {
                    return "Unexpected response code: " + responseCode;
                }

            } catch (Exception e) {
                Log.e("CreateAccountTask", "Error: ", e);
                return "Error: " + e.getMessage();
            } finally {
                // Ensure the connection is properly closed
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(AccountCreationActivity.this, result, Toast.LENGTH_SHORT).show();

            if ("Account created successfully".equals(result)) {
                // Navigate to the LoginActivity on successful account creation
                navigateToLoginActivity();
            }
        }

        private void navigateToLoginActivity() {
            Intent intent = new Intent(AccountCreationActivity.this, LoginActivity.class); // Change to your actual LoginActivity class
            startActivity(intent);
            finish(); // Optional: Close AccountCreationActivity if needed
        }
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(AccountCreationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Close AccountCreationActivity if needed
    }
}