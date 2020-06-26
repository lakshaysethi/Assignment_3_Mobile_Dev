package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobileassignment3.parcel_tracking_app.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.NotificationActivity;
import com.mobileassignment3.parcel_tracking_app.ProfileActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Driver;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;
import java.util.List;

public class DriverMainActivity extends AppCompatActivity {
    private RecyclerView rvMyTask;
    private RecyclerView.Adapter adapterMyTask;
    private RecyclerView.LayoutManager layoutManagerMyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        // Change the actionbar title and icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_person_pin_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        new FirebaseController().getUser(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                getSupportActionBar().setTitle(user.getUsername());
            }
        });

        // Click the action bar title to open the profile activity
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(DriverMainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });

        rvMyTask = findViewById(R.id.rvMyTask);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvMyTask.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerMyTask = new LinearLayoutManager(this);
        rvMyTask.setLayoutManager(layoutManagerMyTask);

        //TODO get the assigned delivery job from firestore

        new FirebaseController().db.collection("users").document(new FirebaseController().getCurrentFirebaseUserObject().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    try{
                     Driver driver = doc.toObject(Driver.class);
                     List<DeliveryJob> parcelDataset = driver.getDeliveryJobList(); // Can be null if no job is assigned yet
                     parcelDataset = parcelDataset == null ? new ArrayList<DeliveryJob>() : parcelDataset;

                     // specify an adapter
                    setAdapterStuff(parcelDataset);
                    }catch (Exception e){
                        Toast.makeText(DriverMainActivity.this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void setAdapterStuff(List<DeliveryJob> parcelDataset) {
        adapterMyTask = new TaskAdapter(this, (ArrayList<DeliveryJob>)parcelDataset);
        rvMyTask.setAdapter(adapterMyTask);
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
        switch (item.getItemId()) {
            case R.id.notification:
                Intent myIntent = new Intent(DriverMainActivity.this, NotificationActivity.class);
                startActivity(myIntent);
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }
}



class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    private ArrayList<DeliveryJob> mDataset;
    private Context mContext;
    private ArrayList<DeliveryJob> deliveryJobArray;
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
    public TaskAdapter(Context context, ArrayList<DeliveryJob> myDataset) {
        mContext = context;
        mDataset = myDataset;
        deliveryJobArray = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_my_task, parent, false);
        TextView title = (TextView) v.findViewById(R.id.cardMyTaskTitle);
        TextView detail = (TextView) v.findViewById(R.id.cardMyTaskDetail);

        MyViewHolder vh = new MyViewHolder(v, title, detail);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textViewTitle.setText(deliveryJobArray.get(position).getListOfParcels().get(0).getDescription());
        holder.textViewDetail.setText(deliveryJobArray.get(position).getStatusString());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    //Alert Dialog
    public void onCreateDialog() {

        //TODO get the estimate time
        String estimateTime = "10 mins";
        String driverSendMessage = "Send Message to Customer:\nYour parcel will be deliveried in "+ estimateTime;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(driverSendMessage)
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO send message

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Closer the alert dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();

    }
}

