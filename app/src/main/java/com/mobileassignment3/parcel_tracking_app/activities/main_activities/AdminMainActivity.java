package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileassignment3.parcel_tracking_app.OldFirebaseController;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseAuthCustom;
import com.mobileassignment3.parcel_tracking_app.MasterListDocument;
import com.mobileassignment3.parcel_tracking_app.NotificationActivity;
import com.mobileassignment3.parcel_tracking_app.ProfileActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.controllers.ReadFromFireStore;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;

import java.util.ArrayList;
import java.util.List;

import static com.mobileassignment3.parcel_tracking_app.MyStaticClass.myStaticObjectsList;

public class AdminMainActivity extends MainActivityForAllUsers {

    Button btnAssign;
    FloatingActionButton btnRefresh;
    static OldFirebaseController mainFirebase = new OldFirebaseController();
    static RecyclerView rvAssignOrder;
    ArrayList<DeliveryJob> jobs = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // new FirebaseController().getdeliveryJobsAssociatedWithAuthenticatedUser();
        //mainFirebase.writeMasterDeliveryJobsToFirestore();
       
        // here I am getting the delivery jobs from the firestore and setting the recyclerview

        adminlistviewUpdate();
        setActionBarStuff();

    }
    private void adminlistviewUpdate() {

        mainFirebase.getAllUsers();

        btnAssign = findViewById(R.id.btnAssign);
        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssignDialog();
            }
        });

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
        MasterListDocument mlObj = new MasterListDocument();

            @Override
            public void onClick(View v) {
                Toast.makeText(AdminMainActivity.this, "Refresing...", Toast.LENGTH_LONG).show();
                 if(mlObj.updateMasterList(AdminMainActivity.this,mlObj)){
                     setRecyclerViewStuff(mlObj.getMasterList());

                 }else{
                     setRecyclerViewStuff(((MasterListDocument)(myStaticObjectsList.get(0))).getMasterList());
                 }

            }
        });
    }
    public void openAssignDialog() {
        AssignDialog dialog = new AssignDialog();

        dialog.show(getSupportFragmentManager(), "Assign dialog");

    }
     public static void assignDriver(String driverUsername, ArrayList<DeliveryJob> selectedJobs) {

          Log.d("JOBS", "AssignDriver: "+selectedJobs.toString());
          mainFirebase.assignParcelToDriver(driverUsername, selectedJobs);
         final Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 getDeliveryJobsListfromFirestore();
             }
         }, 1000);
         if(FirebaseAuthCustom.userlist.get(1).getUsername().equals(driverUsername))
         FirebaseAuthCustom.userlist.get(1).setDeliveryJobList(selectedJobs);
         new FirebaseAuthCustom().updateUser(FirebaseAuthCustom.userlist.get(1),FirebaseAuthCustom.userlist.get(1).getUID());
     }

    private static void getDeliveryJobsListfromFirestore() {
        try{
            new FirebaseController().db.collection("masterDeliveryJobs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d("FIREBASE", document.getId() + " => " + document.getData());
                                    if(document.contains("masterList")){
                                        document.get("masterList");
                                        List<DeliveryJob> Djal = document.toObject(MasterListDocument.class).masterList;
                                        List<DeliveryJob> jobsWithNoDriver = new ArrayList();
                                        for (DeliveryJob jobIterator : Djal){
                                            if (jobIterator.getAssignedDriver() == null){
                                                jobsWithNoDriver.add(jobIterator);
                                            }
                                        }

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
    }


    public static ArrayList<DeliveryJob> getSelectedJobs() {

         OrderAdapter adapter = (OrderAdapter) rvAssignOrder.getAdapter();
         ArrayList<DeliveryJob> jobs = new ArrayList<>();
         for (int x = 0; x<rvAssignOrder.getChildCount();x++){
             CheckBox cb = (CheckBox)rvAssignOrder.getChildAt(x).findViewById(R.id.cbAssignOrder);
             if(cb.isChecked()){
                 jobs.add(adapter.getJobAt(x));
                 Log.d("JOBS", "getSelectedJobs: " + jobs.toString());
             }
         }
         return jobs;
     }
    void setRecyclerViewStuff(List<DeliveryJob> Djal){

         rvAssignOrder = findViewById(R.id.rvAssignOrder);
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
    @Override
    // Inflate the menu; this adds items to the action bar if it is present.
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    // implemented the menu item  STARTS - notification activity :
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

        getSupportActionBar().setTitle(new FirebaseAuthCustom().getCurrentParcelAppUser().get(0).getUsername());

        // Click the action bar title to open the profile activity
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AdminMainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });

    }
    public static class AssignDialog extends AppCompatDialogFragment {
        private EditText editDriverUsername;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater =  getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_dialog, null);
            builder.setView(view)
                    .setTitle("Assign to driver")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String driverUsername = editDriverUsername.getText().toString();

                            Log.d("JOBS", "AssignDriver: "+ getSelectedJobs().toString());
                            assignDriver(driverUsername, getSelectedJobs());

                        }
                    });

            editDriverUsername = view.findViewById(R.id.driverUsername);

            return builder.create();
        }




    }
}


class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<DeliveryJob> deliveryJobArray;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView textViewTitle;
        public TextView textViewDetail;

        public MyViewHolder(CardView cardview, TextView tv1, TextView tv2) {
            super(cardview);
            cardView = cardview;
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

        //This was never used because it was the wrong way of getting checked parcels
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DeliveryJob jobIterator : deliveryJobArray){
                    if (jobIterator.getListOfParcels().get(0).getDescription().equals(vh.textViewTitle.getText())){
                        int e = deliveryJobArray.indexOf(jobIterator);
                        Log.d("CLICK", "Parcel clicked: " + deliveryJobArray.get(e).getTrackingNumber());
//                        selectedParcels.add(deliveryJobArray.get(e).getTrackingNumber());
                    }
                }
            }
        });
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

    public DeliveryJob getJobAt(int position) {
        return deliveryJobArray.get(position);
    }
}
