package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.mobileassignment3.parcel_tracking_app.AssignDialog;
import com.mobileassignment3.parcel_tracking_app.MasterListDocument;
import com.mobileassignment3.parcel_tracking_app.NotificationActivity;
import com.mobileassignment3.parcel_tracking_app.ProfileActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity implements AssignDialog.assignDialogListener{

    Button btnAssign;
    FirebaseController mainFirebase = new FirebaseController();
    List<String> selectedParcels = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // new FirebaseController().getdeliveryJobsAssociatedWithAuthenticatedUser();

        setActionBarStuff();
        // here I am getting the delivery jobs from the firestore and setting the recyclerview
        getDeliveryJobsListfromFirestore();
        mainFirebase.getAllUsers();
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
         btnAssign.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 assignDialog();
             }
         });

    }


    public void assignDialog() {
        AssignDialog dialog = new AssignDialog();
        dialog.show(getSupportFragmentManager(), "Assign dialog");
        ArrayList<DeliveryJob> jobs = getSelectedJobs();
    }


    //TODO Make the assigndriver actually assign to the driver
    public void assignDriver(String driverUsername) {
         Toast.makeText(AdminMainActivity.this, "Driver is " + driverUsername, Toast.LENGTH_SHORT).show();
         mainFirebase.assignParcelToDriver(driverUsername, selectedParcels);
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

    // Get selected DeliveryJob in the RecyclerView
    ArrayList<DeliveryJob> getSelectedJobs() {
        RecyclerView rvAssignOrder = findViewById(R.id.rvAssignOrder);
        OrderAdapter adapter = (OrderAdapter) rvAssignOrder.getAdapter();
        ArrayList<DeliveryJob> jobs = new ArrayList<>();
        for (int x = 0; x<rvAssignOrder.getChildCount();x++){
            CheckBox cb = (CheckBox)rvAssignOrder.getChildAt(x).findViewById(R.id.cbAssignOrder);
            if(cb.isChecked()){
                jobs.add(adapter.getJobAt(x));
            }
        }
        return jobs;
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
        final TextView title = (TextView) v.findViewById(R.id.cardOrderTitle);
        TextView detail = (TextView) v.findViewById(R.id.cardOrderDetail);
        CheckBox check = v.findViewById(R.id.cbAssignOrder);

        final MyViewHolder vh = new MyViewHolder(v, title, detail);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("CLICK", "Parcel clicked: " + deliveryJobArray.get(1).getListOfParcels().get(0).getDescription());
                int e =  deliveryJobArray.indexOf(vh.textViewTitle.getText());
                Log.d("INDEX", Integer.toString(e));
                Log.d("CLICK", "Parcel clicked: " + vh.textViewTitle.getText());

                //                Log.d("CLICK", "Parcel clicked: " + deliveryJobArray.get(0).getTrackingNumber());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textViewTitle.setText(deliveryJobArray.get(position).getListOfParcels().get(0).getDescription());
        holder.textViewDetail.setText(deliveryJobArray.get(position).getStatusString());
//        holder.cbAssignOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                final boolean isChecked = holder.checkBoxparent.isChecked();
//                for (int i=0; i<approvePendingDataArrayList.size();i++) {
//                    if (isChecked) {
//                        if (!arrayListUser.contains(approvePendingDataArrayList.get(position).getmText1()))
//                            arrayListUser.add(i, approvePendingDataArrayList.get(position).getmText1());
//                        arrayData=arrayListUser.toString().replace("[", "").replace("]", "").trim();
//                    } else {
//                        arrayListUser.remove(approvePendingDataArrayList.get(position).getmText1());
//                        arrayData=arrayListUser.toString().replace("[", "").replace("]", "").trim();
//                    }
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return deliveryJobArray.size();
    }

    public DeliveryJob getJobAt(int position) {
        return deliveryJobArray.get(position);
    }
}

