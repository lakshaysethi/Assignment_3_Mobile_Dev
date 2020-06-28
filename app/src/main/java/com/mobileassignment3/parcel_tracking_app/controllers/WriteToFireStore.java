package com.mobileassignment3.parcel_tracking_app.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileassignment3.parcel_tracking_app.MasterListDocument;
import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseAuthCustom;
import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Admin;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Customer;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Driver;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WriteToFireStore extends FirebaseController {
    public void  writeMasterDeliveryJobsToFirestore(){
        //TODO make this function redundent - we should not create random delivery jobs in production
        MasterListDocument masterlist = new MasterListDocument();
        masterlist.setMasterList(returnrandomDeliveryJobs());
        db.collection("masterDeliveryJobs").document("deliveryJobsDocument").set(masterlist);
    }

    private ArrayList<DeliveryJob> returnrandomDeliveryJobs() {
        ArrayList<DeliveryJob> djal  = new ArrayList<DeliveryJob>();
        String[]  senders = {"Danica", "Lakhsay", "John Casey", "Raza", "Obama", "Paul Bartlett", "Dila"};
        String[]  packages = {"Letter", "Laptop", "Jacket", "Certificate", "Backpack", "Payslip", "Vaccine" };
        for(int i=0;i<7;i++) {
            Random rand1 = new Random();Random rand2 = new Random();int n = rand1.nextInt(7);int m = rand2.nextInt(7);
            DeliveryJob nDJ = new DeliveryJob();
            nDJ.addParcel(new Parcel( packages[n] + " from " + senders[m]));
            djal.add(nDJ);
        }
        return djal;
    }

//    public void assignParcelToDriver(final String driverUserName, ArrayList<DeliveryJob> alOfSelectedDjs){
//
//        for (DeliveryJob deliveryJob : Djal) {
//            if (deliveryJob.getTrackingNumber().equals(trackingNumber)){                    //Find the delivery job you want to update
//                for (User thisUser : allUsers) {
//                    if (thisUser.getUsername().equals(driverUserName)){                     //Find the driver object you want to assign to the job
//                        Driver driverToAssign = (Driver)thisUser;
//                        deliveryJob.setAssignedDriver(driverToAssign);                      //and assign the entered driver to it
//                        updateMasterDeliveryJobList(Djal);                                  //update the masterDeliveryJobList
//                    }
//                }
//            }
//            else{
//                Log.d("Firebase error", "Entered driver not found");
//            }
//        }
//
//    }


    public void writedeliveryJobsToUser(ArrayList<DeliveryJob> deliveryJobArrayList, final String uuid, final int userType){

        final ArrayList<DeliveryJob> djal = deliveryJobArrayList;
        db.collection(FirebaseAuthCustom.userDatabaseToUse).document(uuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();



                    if (userType == User.DRIVER) {
//            user = (Driver)user;
                        Driver parcelappuser =    doc.toObject(Driver.class);
                        parcelappuser.setDeliveryJobList(djal);

                        new FirebaseAuthCustom().updateUser(parcelappuser,uuid);
                        db.collection(FirebaseAuthCustom.userDatabaseToUse).document(uuid).set(parcelappuser);

                    } else if (userType == User.RECIEVER) {
                        Customer parcelappuser    =  doc.toObject(Customer.class);
                        parcelappuser.setDeliveryJobList(djal);

                        new FirebaseAuthCustom().updateUser(parcelappuser,uuid);
                        db.collection(FirebaseAuthCustom.userDatabaseToUse).document(uuid).set(parcelappuser);

                    } else {
                        Admin parcelappuser   =  doc.toObject(Admin.class);
                        parcelappuser.setDeliveryJobList(djal);

                        new FirebaseAuthCustom().updateUser(parcelappuser,uuid);
                        //db.collection(FirebaseAuthCustom.userDatabaseToUse).document(uuid).set(parcelappuser);
                    }




                }else{
                    Log.d("Error","Firebasecontroller error");
                }
            }
        });

    }



}
