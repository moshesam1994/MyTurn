package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AppointmentActivity extends AppCompatActivity {
    private EditText editTextCustomerName, editTextDate;
    private Button buttonBookAppointment;
    private GridLayout gridLayoutTimeSlots;
    private String selectedTimeSlot;
    private FirebaseFirestore db;
    private String[] timeSlots = {
            "09:00-09:30", "09:30-10:00", "10:00-10:30", "10:30-11:00",
            "11:00-11:30", "11:30-12:00", "12:00-12:30", "12:30-13:00",
            "13:00-13:30", "13:30-14:00", "14:00-14:30", "14:30-15:00",
            "15:00-15:30", "15:30-16:00", "16:00-16:30", "16:30-17:00",
            "17:00-17:30", "17:30-18:00", "18:00-18:30", "18:30-19:00"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        editTextCustomerName = findViewById(R.id.edit_text_customer_name);
        editTextDate = findViewById(R.id.edit_text_date);
        buttonBookAppointment = findViewById(R.id.button_book_appointment);
        gridLayoutTimeSlots = findViewById(R.id.grid_layout_time_slots);

        db = FirebaseFirestore.getInstance();

        // Set up date picker for date EditText
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up time slot buttons
        setupTimeSlots();

        buttonBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookAppointment();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AppointmentActivity.this,
                (view, year, month, dayOfMonth) -> {
                    String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                    editTextDate.setText(date);
                    updateAvailableTimeSlots(date);  // Update available time slots for the selected date
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setupTimeSlots() {
        for (String timeSlot : timeSlots) {
            Button button = new Button(this);
            button.setText(timeSlot);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTimeSlot = timeSlot;
                    highlightSelectedTimeSlot(button);
                }
            });
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            button.setLayoutParams(params);
            gridLayoutTimeSlots.addView(button);
        }
    }

    private void highlightSelectedTimeSlot(Button selectedButton) {
        for (int i = 0; i < gridLayoutTimeSlots.getChildCount(); i++) {
            View child = gridLayoutTimeSlots.getChildAt(i);
            if (child instanceof Button) {
                child.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
        selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    private void updateAvailableTimeSlots(String date) {
        if (date.isEmpty()) {
            return;
        }

        db.collection("appointments")
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (String timeSlot : timeSlots) {
                            Button button = findButtonByTimeSlot(timeSlot);
                            if (button != null) {
                                boolean isAvailable = true;
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (timeSlot.equals(document.getString("time"))) {
                                        isAvailable = false;
                                        break;
                                    }
                                }
                                button.setEnabled(isAvailable);
                                button.setBackgroundColor(isAvailable ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray));
                            }
                        }
                    } else {
                        Log.e("AppointmentActivity", "Error getting appointments", task.getException());
                    }
                });
    }

    private Button findButtonByTimeSlot(String timeSlot) {
        for (int i = 0; i < gridLayoutTimeSlots.getChildCount(); i++) {
            View child = gridLayoutTimeSlots.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                if (timeSlot.equals(button.getText().toString())) {
                    return button;
                }
            }
        }
        return null;
    }

    private void bookAppointment() {
        String customerName = editTextCustomerName.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String time = selectedTimeSlot;

        if (customerName.isEmpty() || date.isEmpty() || time == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        db.collection("appointments")
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {

                            saveAppointment(customerName, date, time);
                        } else {
                            Toast.makeText(this, "This time slot is already booked. Please choose another one.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("AppointmentActivity", "Error checking availability", task.getException());
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveAppointment(String customerName, String date, String time) {
        String customerEmail = getIntent().getStringExtra("userEmail"); // קבל את כתובת האימייל של הלקוח מה Intent
        Toast.makeText(this, "appiment to: " +customerEmail, Toast.LENGTH_SHORT).show();

        String id = UUID.randomUUID().toString();
        Appointment appointment = new Appointment(id, customerName, customerEmail, date, time, "Service");

        Log.d("eeeeee", "Booking appointment with details: " +
                "ID: " + id + ", CustomerName: " + customerName + ", CustomerEmail: " + customerEmail +
                ", Date: " + date + ", Time: " + time);

        db.collection("appointments").document(id).set(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment booked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("customerName", customerName);
                    intent.putExtra("appointmentDate", date);
                    intent.putExtra("appointmentTime", time);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AppointmentActivity", "Error booking appointment", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
