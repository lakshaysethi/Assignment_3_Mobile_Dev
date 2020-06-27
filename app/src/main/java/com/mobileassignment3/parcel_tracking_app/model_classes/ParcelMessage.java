package com.mobileassignment3.parcel_tracking_app.model_classes;

public class ParcelMessage {
    public String title;
    public String content;
    public String driverEmail;
    public String receiverEmail;
    public long timestamp;

    public ParcelMessage() {}

    public ParcelMessage(String title, String content, String driverEmail, String receiverEmail, long timestamp) {
        this.title = title;
        this.content = content;
        this.driverEmail = driverEmail;
        this.receiverEmail = receiverEmail;
        this.timestamp = timestamp;
    }
}
