package com.example.myapplication;
public class Appointment {
    private String id;
    private String customerName;
    private String customerEmail;
    private String date;
    private String time;
    private String serviceType;

    // Required default constructor for Firestore
    public Appointment() {
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    }

    public Appointment(String id, String customerName, String customerEmail, String date, String time, String serviceType) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.date = date;
        this.time = time;
        this.serviceType = serviceType;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    // Optional: toString method for debugging
    @Override
    public String toString() {
        return "Appointment{" +
                "id='" + id + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}

