package com.example.myapplication.Component01;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccountCreationActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

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
        });
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
                // Set up the URL connection to your backend API
                URL url = new URL("http://192.168.250.214:44300/Auth/register");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Create JSON object to send to the server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);  // Username input from user
                jsonObject.put("password", password);
                jsonObject.put("email", email);

                // Write the JSON object to the request body
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                // Get the response code to check if the request was successful
                int responseCode = urlConnection.getResponseCode();
                Log.d("CreateAccountTask", "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Account created successfully";
                } else {
                    return "Failed to create account. Response code: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
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
        }
    }
}