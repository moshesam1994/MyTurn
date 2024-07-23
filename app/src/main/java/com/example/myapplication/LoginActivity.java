package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private TextView textViewRegister;
    private RadioGroup radioGroupRole;

    private String email;
    private String password;

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//
//        if(currentUser != null){
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            Log.d("ddd",currentUser.toString());
//            intent.putExtra("userEmail", currentUser.getEmail());
//            intent.putExtra("userRole", "manager");
//
//
//            startActivity(intent);
//            finish();
//
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonLogin = findViewById(R.id.button_login);
        textViewRegister = findViewById(R.id.register_now_login);
        radioGroupRole = findViewById(R.id.radio_group_role_login);

        mAuth = FirebaseAuth.getInstance();

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
        });

        buttonLogin.setOnClickListener(v -> {
            email = editTextEmail.getText().toString().trim();
            password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            signInWithEmailAndPassword(email, password);
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Fetch user role based on user ID
                            fetchUserRole(user.getUid());
                        } else {
                            Toast.makeText(LoginActivity.this, "User is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String email, String role) {
        SharedPreferences sharedPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userEmail", email);
        editor.putString("userRole", role);
        editor.apply();
    }

    private void fetchUserRole(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    String role = document.getString("role");
                    if (role != null) {
                        saveUserData(role, email);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userRole", role);
                        intent.putExtra("userEmail", email);
                        intent.putExtra("userPassword", password);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User role not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User document not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Failed to get user role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to get user role.", task.getException());
            }
        });
    }


    private String getUserRoleFromPrefs() {
        SharedPreferences sharedPref = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPref.getString("userRole", null);
    }

    private void navigateToMainActivity(String email, String role) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userEmail", email);
        intent.putExtra("userRole", role);
        startActivity(intent);
        finish();
    }
}
