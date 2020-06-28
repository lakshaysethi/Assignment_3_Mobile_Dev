package com.mobileassignment3.parcel_tracking_app.controllers;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileassignment3.parcel_tracking_app.MasterListDocument;
import com.mobileassignment3.parcel_tracking_app.OldFirebaseController;
import com.mobileassignment3.parcel_tracking_app.SplashActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;

import java.util.ArrayList;
import java.util.List;

public class ReadFromFireStore extends FirebaseAuthCustom {


    public void getAndSetLatestDeliveryMasterJobsListfromFirestore(final MasterListDocument mlObj) {
        try{
            db.collection("masterDeliveryJobs").document("deliveryJobsDocument").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    MasterListDocument mlObjFromFireStore = documentSnapshot.toObject(MasterListDocument.class);
                    ArrayList<DeliveryJob> masterDeliveryJobsList = mlObjFromFireStore.getMasterList();
                    mlObj.setMasterList(masterDeliveryJobsList);
                }


            });


        }catch (Exception e){
            Toast.makeText(new SplashActivity(), "", Toast.LENGTH_SHORT).show();

            Log.w("Firebase error", "Error getting documents."+e.toString());

        }

    }

}
