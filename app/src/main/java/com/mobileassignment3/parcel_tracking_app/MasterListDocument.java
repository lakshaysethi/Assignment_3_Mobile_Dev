package com.mobileassignment3.parcel_tracking_app;

import android.app.Activity;
import android.widget.Toast;

import com.mobileassignment3.parcel_tracking_app.activities.main_activities.AdminMainActivity;
import com.mobileassignment3.parcel_tracking_app.controllers.ReadFromFireStore;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;

import java.util.ArrayList;
import java.util.List;

public class MasterListDocument {
   public  ArrayList<DeliveryJob> masterList;
    public MasterListDocument(){}

    public ArrayList<DeliveryJob> getMasterList() {
        return masterList;
    }

    public void setMasterList(ArrayList<DeliveryJob> masterList) {
        this.masterList = masterList;
    }

    public boolean updateMasterList(Activity activity,MasterListDocument mlObj) {

        if(mlObj.getMasterList()==null){
            new ReadFromFireStore().getAndSetLatestDeliveryMasterJobsListfromFirestore(mlObj);
            Toast.makeText(activity, "Please wait getting list from DB", Toast.LENGTH_LONG).show();
            return false;
        }else{

            Toast.makeText(activity, "MasterList Update Complete", Toast.LENGTH_LONG).show();
            return true;
        }

    }
}