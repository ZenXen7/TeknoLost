package com.example.teknolost;

import java.io.Serializable;

public class Notification implements Serializable {
    private String title;
    private String message;
    private String timestamp;
    private String requestId;

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(Notification.class)
    }



    public Notification(String title, String message, String timestamp, String requestId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.requestId = requestId;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

