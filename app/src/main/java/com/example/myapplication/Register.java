package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText editTextPassword, editTextEmail;
    private Button buttonReg;
    private RadioGroup radioGroupRole;
    private FirebaseAuth mAuth;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextPassword = findViewById(R.id.Register_text_password);
        editTextEmail = findViewById(R.id.Register_text_email);
        buttonReg = findViewById(R.id.button_register);
        textView = findViewById(R.id.loginNow);
        radioGroupRole = findViewById(R.id.radio_group_role);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                password = editTextPassword.getText().toString();
                email = editTextEmail.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkIfManagerExistsAndRegister(email, password);
            }
        });
    }

    private void checkIfManagerExistsAndRegister(String email, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("role", "manager")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean managerExists = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            managerExists = true;
                            break;
                        }

                        if (managerExists && radioGroupRole.getCheckedRadioButtonId() == R.id.radio_button_manager) {
                            Toast.makeText(Register.this, "There can only be one manager.", Toast.LENGTH_SHORT).show();
                        } else {
                            registerUser(email, password);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String role = (radioGroupRole.getCheckedRadioButtonId() == R.id.radio_button_manager) ? "manager" : "customer";
                                saveUserRole(user.getUid(), role);
                            }
                            Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserRole(String userId, String role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("role", role);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User role saved successfully.");
                    } else {
                        Log.w(TAG, "Error saving user role.", task.getException());
                    }
                });
    }
}
