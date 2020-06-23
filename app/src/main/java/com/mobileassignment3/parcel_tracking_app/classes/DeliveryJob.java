package com.mobileassignment3.parcel_tracking_app.classes;

import com.mobileassignment3.parcel_tracking_app.classes.user.Customer;
import com.mobileassignment3.parcel_tracking_app.classes.user.Driver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class DeliveryJob {
    final int DELIVERED = 1;
    final int NOT_DELIVERED = 0;

    int status;
    UUID trackingNumber;
    ArrayList<Parcel> listOfParcels = new ArrayList<Parcel>();
    Driver assignedDriver;
    Customer receiver;
    Date targetDeliveryTime;


    public DeliveryJob() {
        this.status = NOT_DELIVERED;
        this.trackingNumber = UUID.randomUUID();


    }


    public DeliveryJob(   Driver assignedDriver, Customer receiver, Date targetDeliveryTime) {
        this.status = NOT_DELIVERED;
        this.trackingNumber = UUID.randomUUID();;

        this.assignedDriver = assignedDriver;
        this.receiver = receiver;
        this.targetDeliveryTime = targetDeliveryTime;
    }

    public DeliveryJob(  Date targetDeliveryTime) {
        this.trackingNumber = UUID.randomUUID();;

        this.targetDeliveryTime = targetDeliveryTime;
        this.status = NOT_DELIVERED;
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTrackingNumber() {
        return String.valueOf(trackingNumber);
    }

    public void setTrackingNumber(UUID trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public List<Parcel> getListOfParcels() {
        return listOfParcels;
    }

    public void addParcel(Parcel parcel) {
        this.listOfParcels.add(parcel);
    }

    public Driver getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(Driver assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public Customer getReceiver() {
        return receiver;
    }

    public void setReceiver(Customer receiver) {
        this.receiver = receiver;
    }

    public Date getTargetDeliveryTime() {
        return targetDeliveryTime;
    }

    public void setTargetDeliveryTime(Date targetDeliveryTime) {
        this.targetDeliveryTime = targetDeliveryTime;
    }

    @Override
    public String toString() {
        return "DeliveryJob{" +
                "status='" + status + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", ListOfParcels=" + listOfParcels +
                ", assignedDriver=" + assignedDriver +
                ", receiver=" + receiver +
                ", targetDeliveryTime=" + targetDeliveryTime +
                '}';
    }
}
