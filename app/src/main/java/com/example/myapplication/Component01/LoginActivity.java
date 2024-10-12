import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private CheckBox checkboxRememberMe;
    private TextView textViewRegister;
    private OkHttpClient client = new OkHttpClient();
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide the action bar if it's present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        checkboxRememberMe = findViewById(R.id.checkboxRememberMe);
        textViewRegister = findViewById(R.id.textViewRegister);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadSavedCredentials(); // Load saved credentials if available

        // Set up the login button click listener
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate input and authenticate the user if valid
                if (isInputValid(username, password)) {
                    authenticateUser(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter username and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the register link click listener
        textViewRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, AccountCreationActivity.class);
            startActivity(registerIntent);
        });
    }


    private boolean isInputValid(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    // Create a JSON object with the username and password to send to the API
    private void authenticateUser(String username, String password) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            String json = jsonObject.toString();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url("https://pasindu99-001-site1.etempurl.com/Auth/login")  // API endpoint
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseJson = new JSONObject(response.body().string());

                            // Parse the response
                            String token = responseJson.getString("token");
                            String userId = responseJson.getString("userId");
                            String email = responseJson.getString("email");
                            JSONArray rolesArray = responseJson.getJSONArray("roles");
                            String status = responseJson.getString("status");
                            String username = responseJson.getString("username");


                            // Convert roles JSONArray to a String array
                            String[] roles = new String[rolesArray.length()];
                            for (int i = 0; i < rolesArray.length(); i++) {
                                roles[i] = rolesArray.getString(i);
                            }

//                            // Use a final boolean array to track if the user is a Customer
//                            final boolean[] isCustomer = {false};
//                            for (String role : roles) {
//                                if (role.equals("Customer")) {
//                                    isCustomer[0] = true; // Update the value in the array
//                                    break;
//                                }
//                            }

                            // Use runOnUiThread to update UI elements
                            runOnUiThread(() -> {
                                 // Access the value from the array
                                    Toast.makeText(LoginActivity.this, username + ": Login successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("TOKEN", token);      // Pass token to MainActivity
                                    intent.putExtra("USERNAME", username); // Pass username to MainActivity
                                    intent.putExtra("USER_ID", userId);    // Pass user ID to MainActivity
                                    intent.putExtra("EMAIL", email);        // Pass email to MainActivity
                                    intent.putExtra("ROLES", roles);        // Pass roles to MainActivity
                                    intent.putExtra("STATUS", status);      // Pass status to MainActivity

                                    // Save credentials if Remember Me is checked
                                    if (checkboxRememberMe.isChecked()) {
                                        saveCredentials(username, password);
                                    } else {
                                        clearCredentials(); // Clear credentials if not checked
                                    }

                                    startActivity(intent);
                                    finish();
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Retrieve the saved username and password from SharedPreferences
    private void loadSavedCredentials() {
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
        editTextUsername.setText(savedUsername); // Set saved username
        editTextPassword.setText(savedPassword); // Set saved password
        checkboxRememberMe.setChecked(!savedUsername.isEmpty()); // Check the box if there's a saved username
    }

    //Save the username and password in SharedPreferences
    private void saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username); // Save username
        editor.putString(KEY_PASSWORD, password); // Save password
        editor.apply();
    }

    // Remove saved username and password from SharedPreferences
    private void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME); // Remove saved username
        editor.remove(KEY_PASSWORD); // Remove saved password
        editor.apply();
    }
}