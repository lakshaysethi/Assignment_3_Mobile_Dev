package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;

import java.util.ArrayList;

public class MainActivityForAllUsers extends AppCompatActivity {



    public void setArraylistInAdapter(RecyclerView rvParcel, ArrayList<DeliveryJob> djal) {
        ArrayList<DeliveryJob> deliveryJobsAssociatedWithAuthenticatedUser = djal;


        RecyclerView.Adapter adapterParcel = new RecieverDeliveryJobAdapter(this,deliveryJobsAssociatedWithAuthenticatedUser);
        rvParcel.setAdapter(adapterParcel);

    }

}
