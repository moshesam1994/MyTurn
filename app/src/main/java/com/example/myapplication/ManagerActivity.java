package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {
    private EditText editTextDate;
    private Button buttonViewAppointments;
    private ListView listViewAppointments;
    private FirebaseFirestore db;
    private List<Appointment> appointmentList;
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        editTextDate = findViewById(R.id.edit_text_date);
        buttonViewAppointments = findViewById(R.id.button_view_appointments);
        listViewAppointments = findViewById(R.id.list_view_appointments);

        db = FirebaseFirestore.getInstance();
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, appointmentList);
        //listViewAppointments.setAdapter(appointmentAdapter);

        buttonViewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAppointments();
            }
        });
    }

    private void viewAppointments() {
        String date = editTextDate.getText().toString().trim();
        if (date.isEmpty()) {
            Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("appointments")
                .whereEqualTo("date", date)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    appointmentList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Appointment appointment = doc.toObject(Appointment.class);
                            appointmentList.add(appointment);
                        }
                        appointmentAdapter.notifyDataSetChanged();
                    }
                });
    }
}
