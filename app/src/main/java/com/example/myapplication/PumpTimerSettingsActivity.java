package com.example.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class PumpTimerSettingsActivity extends AppCompatActivity {

    private TimePicker startTimePicker;
    private TimePicker endTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_timer_settings);

        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);

        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        ImageButton backButton = findViewById(R.id.backToDashboardButton);
        backButton.setOnClickListener(v -> finish());

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            String apiKey = sharedPreferences.getString("apiKey", "");

            if (email.isEmpty() || apiKey.isEmpty()) {
                Toast.makeText(PumpTimerSettingsActivity.this, "No data user found", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://192.168.1.8/backendpiscina/logout.php"; //

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("logged", "false");
                                editor.putString("name", "");
                                editor.putString("email", "");
                                editor.putString("apiKey", "");
                                editor.putString("phone", "");
                                editor.putString("lastName", "");
                                editor.apply();

                                // Redirigir al login
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(PumpTimerSettingsActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(PumpTimerSettingsActivity.this, "Error in logout request", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("apiKey", apiKey);
                    return params;
                }
            };
            queue.add(stringRequest);
        });

        Button saveButton = findViewById(R.id.saveTimerSettingsButton);
        saveButton.setOnClickListener(v -> saveTimerSettings());
    }

    private void saveTimerSettings() {
        int startHour = startTimePicker.getHour();
        int startMinute = startTimePicker.getMinute();
        int endHour = endTimePicker.getHour();
        int endMinute = endTimePicker.getMinute();



        String startTime = String.format("%02d:%02d", startHour, startMinute);
        String endTime = String.format("%02d:%02d", endHour, endMinute);


        Intent resultIntent = new Intent();
        resultIntent.putExtra("START_TIME", startTime);
        resultIntent.putExtra("END_TIME", endTime);
        setResult(Activity.RESULT_OK, resultIntent);

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
        finish();
    }
}
