package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mobileassignment3.parcel_tracking_app.FirebaseAuthCustom;
import com.mobileassignment3.parcel_tracking_app.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.NotificationActivity;
import com.mobileassignment3.parcel_tracking_app.ProfileActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.ReceiverMapsActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;
import com.mobileassignment3.parcel_tracking_app.model_classes.ParcelMessage;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;
import java.util.Date;

public class ReceiverMainActivity extends MainActivityForAllUsers {

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

        makeDialogue();
    
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ReceiverMainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });
    }
        void makeDialogue(){
            new FirebaseAuthCustom().getUser(new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    getSupportActionBar().setTitle(user.getUsername());

                    // Lisnte to parcel notification messages
                    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                    long lastReceiveTimestamp = preferences.getLong("last_message_update", 0);
                    new FirebaseController().listenToMessage(user.getEmail(), lastReceiveTimestamp, new OnSuccessListener<ParcelMessage>() {
                        @Override
                        public void onSuccess(ParcelMessage parcelMessage) {
                            // Message received from driver
                            onCreateDialog(parcelMessage);
                        }
                    });
                }
            });
        }


    private boolean isRunning;

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    //Alert Dialog
    public void onCreateDialog(ParcelMessage message) {
        if (!isRunning) {
            Log.w("ReceiverMainActivity", "App paused, don't show dialog or it crashes");
            return;
        }
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putLong("last_message_update", new Date().getTime());  //set a timestamp to only get the latest message
        editor.apply();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message.title)
                .setMessage(message.content)
                .setPositiveButton("Yay!!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
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




}


class RecieverDeliveryJobAdapter extends RecyclerView.Adapter<RecieverDeliveryJobAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<DeliveryJob> mDataset;


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
        holder.textViewDetail.setText(firstparcel.getTypeString());
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
