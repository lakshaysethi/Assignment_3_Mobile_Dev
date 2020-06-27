package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileassignment3.parcel_tracking_app.AssignDialog;
import com.mobileassignment3.parcel_tracking_app.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.MasterListDocument;
import com.mobileassignment3.parcel_tracking_app.NotificationActivity;
import com.mobileassignment3.parcel_tracking_app.ProfileActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;

import java.util.List;

public class AdminMainActivity extends MainActivityForAllUsers implements AssignDialog.assignDialogListener{

    Button btnAssign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // new FirebaseController().getdeliveryJobsAssociatedWithAuthenticatedUser();

        setActionBarStuff();
        // here I am getting the delivery jobs from the firestore and setting the recyclerview
        getDeliveryJobsListfromFirestore();


    }

    private void getDeliveryJobsListfromFirestore() {

        try{
            new FirebaseController().db.collection("masterDeliveryJobs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FIREBASE", document.getId() + " => " + document.getData());
                                    if(document.contains("masterList")){
                                        document.get("masterList");
                                        List<DeliveryJob> Djal = document.toObject(MasterListDocument.class).masterList;
                                        setRecyclerViewStuff( Djal);
                                    }
                                }
                            } else {
                                Log.w("Firebase error", "Error getting documents.", task.getException());
                            }
                        }
                    });

        }catch (Exception e){
            Log.w("Firebase error", "Error getting documents.");

        }

        //new FirebaseController().getdeliveryJobsAssociatedWithAuthenticatedUser();

        //Temp implementation to show dialog for input
        btnAssign = findViewById(R.id.btnAssign);
        // btnAssign.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         assignDialog();
        //     }
        // });


    }


    public void assignDialog() {
        AssignDialog dialog = new AssignDialog();
        dialog.show(getSupportFragmentManager(), "Assign dialog");

    }


    //TODO Make the assigndriver actually assign to the driver
    
    public void assignDriver(String driverUsername) {
        // Toast.makeText(AdminMainActivity.this, "Driver is " + driverUsername, Toast.LENGTH_SHORT).show();
        // new FirebaseController().assignParcelToDriver(driverUsername);
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
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.notification:
            Intent myIntent = new Intent(AdminMainActivity.this, NotificationActivity.class);
            startActivity(myIntent);
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    void setActionBarStuff(){
        // Change the actionbar title and icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_person_pin_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getSupportActionBar().setTitle(new FirebaseController().getCurrentFirebaseUserObject().getEmail());

        // Click the action bar title to open the profile activity
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AdminMainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });

    }

    void setRecyclerViewStuff(List<DeliveryJob> Djal){

        RecyclerView rvAssignOrder = findViewById(R.id.rvAssignOrder);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvAssignOrder.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManagerAssignOrder = new LinearLayoutManager(this);
        rvAssignOrder.setLayoutManager(layoutManagerAssignOrder);

        // specify an adapter

        //updateDeliveryJobArrayList(deliveryJobArrayListDataset);
        RecyclerView.Adapter adapterAssignOrder = new OrderAdapter(Djal);
        rvAssignOrder.setAdapter(adapterAssignOrder);

    }

}


class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<DeliveryJob> deliveryJobArray;
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
    public OrderAdapter(List<DeliveryJob> myDataset) {
        deliveryJobArray = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_assign_order, parent, false);
        TextView title = (TextView) v.findViewById(R.id.cardOrderTitle);
        TextView detail = (TextView) v.findViewById(R.id.cardOrderDetail);

        MyViewHolder vh = new MyViewHolder(v, title, detail);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textViewTitle.setText(deliveryJobArray.get(position).getListOfParcels().get(0).getDescription());
        holder.textViewDetail.setText(deliveryJobArray.get(position).getStatusString());
    }

    @Override
    public int getItemCount() {
        return deliveryJobArray.size();
    }
}

