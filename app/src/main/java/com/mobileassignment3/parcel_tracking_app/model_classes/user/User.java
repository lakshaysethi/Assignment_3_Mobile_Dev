package com.mobileassignment3.parcel_tracking_app.model_classes.user;

import android.provider.ContactsContract;

import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;

import java.util.ArrayList;
import java.util.List;


public class User {

    public    final static int DRIVER = 1001;
    public final static int RECIEVER = 1002;
    public final static int ADMIN = 1003;
    List<DeliveryJob> deliveryJobList;

    public List<DeliveryJob> getDeliveryJobList() {
        return deliveryJobList;
    }

    public void setDeliveryJobList(List<DeliveryJob> deliveryJobList) {
        this.deliveryJobList = deliveryJobList;
    }

    String email;
    String username;

    public ArrayList<Integer> typeArray = new ArrayList<Integer>();


    public void setTypeArray(ArrayList<Integer> typeArray) {
        this.typeArray = typeArray;
    }

    public ArrayList<Integer> getTypeArray() {
        return typeArray;
    }
//    public int getPrimaryType() {
//            return this.typeArray.get(0);
//    }
    public void setType(int type) {
        if(!this.typeArray.isEmpty()){
            this.typeArray.set(0, type);

        }else{
            this.typeArray.add(0,type);
        }
    }
//
//    public void setSecondaryType(int type) {
//
//        if(this.typeArray.get(0)!=null){
//            this.typeArray.set(0, type);
//
//        }else{
//            this.typeArray.add(0,type);
//        }
//    }
//
//    public void setThirdType(int type) {
//
//        if(this.typeArray.get(0)!=null){
//            this.typeArray.set(0, type);
//
//        }else{
//            this.typeArray.add(0,type);
//        }
//    }



    public User(String email, String username) {
        this.email = email;
        this.username = username;

    }

    public User(String username) {
        this.username = username;

    }

    public User() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @Override
    public String toString() {
        return "User{" +
                "email=" + email +
                ", username='" + username + '\'' +
                '}';
    }
}
