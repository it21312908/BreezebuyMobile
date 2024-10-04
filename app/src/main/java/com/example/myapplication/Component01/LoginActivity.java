package com.example.myapplication.Component01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Load user data from JSON
        loadUserData();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (isInputValid(username, password)) {
                    authenticateUser(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter username and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserData() {
        try {
            InputStream inputStream = getAssets().open("user.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("passwordHash");
                String status = jsonObject.getString("status");
                JSONArray rolesArray = jsonObject.getJSONArray("roles");
                String role = rolesArray.getJSONObject(0).getString("role");

                users.add(new User(username, password, status, role));
            }

            reader.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputValid(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private void authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if (user.getStatus().equals("active") && user.getRole().equals("Customer")) {
                    Toast.makeText(LoginActivity.this, username + ": Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USERNAME", user.getUsername());
                    intent.putExtra("STATUS", user.getStatus());
                    startActivity(intent);
                    finish();
                } else if (!user.getStatus().equals("active")) {
                    Toast.makeText(LoginActivity.this, username + ": Account is deactivated. Cannot login.", Toast.LENGTH_SHORT).show();
                } else if (!user.getRole().equals("Customer")) {
                    Toast.makeText(LoginActivity.this, username + ": Only customers can log in.", Toast.LENGTH_SHORT).show();
                }
                return; // Exit the loop once we find the user
            }
        }
        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
    }
}