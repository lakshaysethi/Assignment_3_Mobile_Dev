package com.mobileassignment3.parcel_tracking_app;

import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;

import java.util.ArrayList;
import java.util.Random;

public class WriteToFireStore extends FirebaseController{
    public void  writeMasterDeliveryJobsToFirestore(){
        //TODO make this function redundent - we should not create random delivery jobs in production
        db.collection("masterDeliveryJobs").document("lakshay_test_deliveryJobsDoc").update("masterList",returnrandomDeliveryJobs());
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
}
