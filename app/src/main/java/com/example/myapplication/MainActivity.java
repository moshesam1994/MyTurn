package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageButton buttonAppointment;
    private ImageButton buttonProducts;
    private ImageButton buttonAccount;
    private TextView textViewAppointmentDetails;
    private PortfolioFragment portfolioFragment;
    private Button buttonManagerLogin;
    private ImageButton fabAddImage;
    private String userRole;
    private String email;
    private String password;
    private ImageButton buttonLogout;
    private static final int ADD_IMAGE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAccount = findViewById(R.id.button_history);
        buttonAppointment = findViewById(R.id.button_appointment);
        buttonProducts = findViewById(R.id.button_products);
        textViewAppointmentDetails = findViewById(R.id.text_view_appointment_details);
        buttonManagerLogin = findViewById(R.id.button_manager_login);
        fabAddImage = findViewById(R.id.fab_add_image);

        userRole = getIntent().getStringExtra("userRole");
        email = getIntent().getStringExtra("userEmail");
        password = getIntent().getStringExtra("userPassword");
        buttonLogout = findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(v -> logout());




        Log.d(TAG, "User role: " + userRole);
        Log.d(TAG, "User email: " + email);
        Log.d(TAG, "User password: " + password);

        if ("manager".equals(userRole)) {
            buttonManagerLogin.setVisibility(View.VISIBLE);
            fabAddImage.setVisibility(View.VISIBLE);
            textViewAppointmentDetails.setVisibility(View.GONE);

        } else {
            buttonManagerLogin.setVisibility(View.GONE);
            fabAddImage.setVisibility(View.GONE);
            textViewAppointmentDetails.setVisibility(View.VISIBLE);

        }

        fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddImageActivity.startForResult(MainActivity.this, -1, ADD_IMAGE_REQUEST_CODE);
            }
        });

        buttonManagerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                intent.putExtra("userRole", userRole);
                intent.putExtra("userEmail", email);
                intent.putExtra("userPassword", password);
                startActivity(intent);
            }
        });

        buttonAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
                intent.putExtra("userRole", userRole);
                intent.putExtra("userEmail", email);
                intent.putExtra("userPassword", password);
                startActivityForResult(intent, 1);
            }
        });

        buttonProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("userRole", userRole);
                intent.putExtra("userEmail", email);
                intent.putExtra("userPassword", password);
                startActivity(intent);
            }
        });

        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    intent.putExtra("userRole", userRole);
                    intent.putExtra("userEmail", email);
                    intent.putExtra("userPassword", password);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start AccountActivity", e);
                }
            }
        });

        // Load PortfolioFragment
        portfolioFragment = new PortfolioFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.down_fragment_container, portfolioFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String customerName = data.getStringExtra("customerName");
            String appointmentDate = data.getStringExtra("appointmentDate");
            String appointmentTime = data.getStringExtra("appointmentTime");

            String appointmentDetails = "The appointment you made " + customerName + "\nDate: " + appointmentDate + "\nTime: " + appointmentTime;
            textViewAppointmentDetails.setText(appointmentDetails);
        } else if (requestCode == ADD_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String imageUriString = data.getStringExtra("imageUri");
            int imageIndex = data.getIntExtra("imageIndex", -1);

            Log.d(TAG, "Received Image URI: " + imageUriString);
            Log.d(TAG, "Received Image Index: " + imageIndex);

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.down_fragment_container);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Logout", (dialog, which) -> {
            // Clear user data
            clearUserData();

            // Navigate to login or perform any other necessary actions
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close this activity to prevent going back on logout
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User clicked cancel, do nothing
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearUserData() {
        // Clear SharedPreferences or any other stored data
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
