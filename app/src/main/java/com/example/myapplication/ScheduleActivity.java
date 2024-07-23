package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerViewAppointments;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        calendarView = findViewById(R.id.calendarView);
        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);

        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, appointmentList);
        recyclerViewAppointments.setAdapter(appointmentAdapter);

        db = FirebaseFirestore.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                fetchAppointmentsForDate(selectedDate);
            }
        });
    }

    private void fetchAppointmentsForDate(String date) {
        CollectionReference appointmentsRef = db.collection("appointments");
        appointmentsRef.whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            appointmentList.clear();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Appointment appointment = doc.toObject(Appointment.class);
                                appointmentList.add(appointment);
                            }
                            appointmentAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("ScheduleActivity", "Error getting documents: ", task.getException());
                        Toast.makeText(ScheduleActivity.this, "Error getting appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
