package com.mobileassignment3.parcel_tracking_app.model_classes.user;

import android.location.Location;
import android.provider.ContactsContract;

import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;

import java.util.List;

public class Driver extends User {


    List<DeliveryJob> listOfCompletedDeliveries;
    Boolean onlineNow;
    Location currentLocation;


    public Driver(String username, String password) {
        super(username, password);
    }

    public Driver() {
    }



    public List<DeliveryJob> getListOfCompletedDeliveries() {
        return listOfCompletedDeliveries;
    }

    public void setListOfCompletedDeliveries(List<DeliveryJob> listOfCompletedDeliveries) {
        this.listOfCompletedDeliveries = listOfCompletedDeliveries;
    }

    public Boolean getOnlineNow() {
        return onlineNow;
    }

    public void setOnlineNow(Boolean onlineNow) {
        this.onlineNow = onlineNow;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "username='" + username + '\'' +
                '}';
    }
}
