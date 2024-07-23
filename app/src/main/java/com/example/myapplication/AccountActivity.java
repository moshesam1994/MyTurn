package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    private RecyclerView recyclerViewAppointments;
    private RecyclerView recyclerViewPurchases;
    private AppointmentAdapter appointmentAdapter;
    private PurchaseAdapter purchaseAdapter;
    private List<Appointment> appointmentList;
    private List<Purchase> purchaseList;
    private FirebaseFirestore db;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        recyclerViewPurchases = findViewById(R.id.recyclerViewPurchases);

        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPurchases.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        purchaseList = new ArrayList<>();

        appointmentAdapter = new AppointmentAdapter(this, appointmentList);
        purchaseAdapter = new PurchaseAdapter(this, purchaseList);

        recyclerViewAppointments.setAdapter(appointmentAdapter);
        recyclerViewPurchases.setAdapter(purchaseAdapter);

        db = FirebaseFirestore.getInstance();

        userEmail = getIntent().getStringExtra("userEmail");

        fetchAppointmentsForUser();
        fetchPurchasesForUser();
    }

    private void fetchAppointmentsForUser() {
        CollectionReference appointmentsRef = db.collection("appointments");
        appointmentsRef.whereEqualTo("customerEmail", userEmail).get()
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
                        Log.e(TAG, "Error fetching appointments", task.getException());
                    }
                });
    }

    private void fetchPurchasesForUser() {
        CollectionReference purchasesRef = db.collection("purchases");
        purchasesRef.whereEqualTo("email", userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            purchaseList.clear();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Purchase purchase = doc.toObject(Purchase.class);
                                purchaseList.add(purchase);
                            }
                            purchaseAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Error fetching purchases", task.getException());
                    }
                });
    }
}
