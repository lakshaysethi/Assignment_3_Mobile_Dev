package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mobileassignment3.parcel_tracking_app.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.NotificationActivity;
import com.mobileassignment3.parcel_tracking_app.ProfileActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.ReceiverMapsActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;

public class ReceiverMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receiver_main);

        // Change the actionbar title and icon
        // Click the action bar title to open the profile activity
        setActionBarStuff();
       
        setRecyclerViewStuff();
     }

    // implemented the menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // implemented the menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
        switch(item.getItemId()) {
            case R.id.notification:
                Intent myIntent = new Intent(ReceiverMainActivity.this, NotificationActivity.class);
                startActivity(myIntent);
                return(true);

        }
        return(super.onOptionsItemSelected(item));
    }

    void setActionBarStuff(){           
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_person_pin_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        new FirebaseController().getUser(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                getSupportActionBar().setTitle(user.getUsername());
            }
        });
    
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ReceiverMainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });
    }
    void setRecyclerViewStuff(){
        
        RecyclerView rvParcel = findViewById(R.id.rvMyParcel);
        rvParcel.setHasFixedSize(true);

        
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManagerParcel = new LinearLayoutManager(this);
        rvParcel.setLayoutManager(layoutManagerParcel);

        //TODO get the delivery for myparcel  from firestore #5

        // specify an adapter
        new FirebaseController().setArraylistInAdapterOfActivity(rvParcel,this);

    }

    public void setArraylistInAdapter( RecyclerView rvParcel,ArrayList<DeliveryJob> djal) {
        ArrayList<DeliveryJob> deliveryJobsAssociatedWithAuthenticatedUser = djal;


        RecyclerView.Adapter adapterParcel = new RecieverDeliveryJobAdapter(this,deliveryJobsAssociatedWithAuthenticatedUser);
        rvParcel.setAdapter(adapterParcel);
        
    }



}


class RecieverDeliveryJobAdapter extends RecyclerView.Adapter<RecieverDeliveryJobAdapter.MyViewHolder> {
    private ArrayList<DeliveryJob> mDataset;
    private Context mContext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView textViewTitle;
        public TextView textViewDetail;

        public MyViewHolder(CardView v, TextView tv1, TextView tv2) {
            super(v);
            cardView = v;
            textViewTitle = tv1;
            textViewDetail = tv2;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    public RecieverDeliveryJobAdapter(Context context,ArrayList<DeliveryJob> myDataset) {
    mContext = context;

        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecieverDeliveryJobAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_my_parcel, parent, false);
        TextView title = (TextView) v.findViewById(R.id.cardMyParcelTitle);
        TextView detail = (TextView) v.findViewById(R.id.cardMyParcelDetail);

        MyViewHolder vh = new MyViewHolder(v, title, detail);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Parcel firstparcel = mDataset.get(position).getListOfParcels().get(0);
        holder.textViewTitle.setText(firstparcel.getDescription());
        holder.textViewDetail.setText( firstparcel.getType());
       holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO get the destination address of the clicked delivery card
                String address = "154 Carrington Road, Mount Albert";
                Intent myIntent = new Intent(mContext, ReceiverMapsActivity.class);
                myIntent.putExtra(ReceiverMapsActivity.KEY_ADDRESS, address);
                mContext.startActivity(myIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}
