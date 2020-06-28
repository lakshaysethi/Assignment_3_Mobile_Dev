package com.mobileassignment3.parcel_tracking_app.activities.main_activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseAuthCustom;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;

import java.util.ArrayList;

public class MainActivityForAllUsers extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        doOnce();
    }

    private void doOnce() {
        new FirebaseAuthCustom().addCurrentUser_s_Uid_toDatabase();
    }

    public void setArraylistInAdapter(RecyclerView rvParcel, ArrayList<DeliveryJob> djal) {
        ArrayList<DeliveryJob> deliveryJobsAssociatedWithAuthenticatedUser = djal;


        RecyclerView.Adapter adapterParcel = new DeliveryJobAdapter(deliveryJobsAssociatedWithAuthenticatedUser);
        rvParcel.setAdapter(adapterParcel);

    }

}

class DeliveryJobAdapter extends RecyclerView.Adapter<DeliveryJobAdapter.MyViewHolder> {
    private ArrayList<DeliveryJob> deliveryJobArrayListForMainActivityAdapter;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView textViewTitle;
        public TextView textViewDetail;

        public MyViewHolder(CardView cardView, TextView tv1, TextView tv2) {
            super(cardView);
            this.cardView = cardView;
            textViewTitle = tv1;
            textViewDetail = tv2;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    public DeliveryJobAdapter( ArrayList<DeliveryJob> deliveryJobArrayList) {


        deliveryJobArrayListForMainActivityAdapter = deliveryJobArrayList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeliveryJobAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        Parcel firstparcel = deliveryJobArrayListForMainActivityAdapter.get(position).getListOfParcels().get(0);
        holder.textViewTitle.setText(firstparcel.getDescription());
        holder.textViewDetail.setText(firstparcel.getTypeString());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO get the destination address of the clicked delivery card
                String address = "154 Carrington Road, Mount Albert";
                // Intent myIntent = new Intent(mContext, ReceiverMapsActivity.class);
//                myIntent.putExtra(ReceiverMapsActivity.KEY_ADDRESS, address);
//                mContext.startActivity(myIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return deliveryJobArrayListForMainActivityAdapter.size();
    }



}
