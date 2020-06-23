package com.mobileassignment3.parcel_tracking_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileassignment3.parcel_tracking_app.classes.DeliveryJob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvAssignOrder;
    private RecyclerView.Adapter adapterAssignOrder;
    private RecyclerView.LayoutManager layoutManagerAssignOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change the actionbar title and icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_person_pin_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Administrator");

        // Click the action bar title to open the profile activity
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });

        rvAssignOrder = findViewById(R.id.rvAssignOrder);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvAssignOrder.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerAssignOrder = new LinearLayoutManager(this);
        rvAssignOrder.setLayoutManager(layoutManagerAssignOrder);

        // specify an adapter
        List<DeliveryJob> myDataset = getListOfPendingDeliveryJobs();
        adapterAssignOrder = new OrderAdapter(myDataset);
        rvAssignOrder.setAdapter(adapterAssignOrder);
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
            Intent myIntent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(myIntent);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }

    private List<DeliveryJob> getListOfPendingDeliveryJobs() {

         return randomDeliveryjobs();


    }

    private List<DeliveryJob> randomDeliveryjobs() {
        List<DeliveryJob> deliveryJobArray = new ArrayList<DeliveryJob>() ;
        for (int i=0;i<8;i++){
            deliveryJobArray.add(  new DeliveryJob("NEW ORDER","00"+i) );
        }
        return deliveryJobArray;
    };

}


class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private DeliveryJob[] deliveryJobArray;

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
        holder.textViewTitle.setText(deliveryJobArray[position].getTrackingNumber());
        holder.textViewDetail.setText(deliveryJobArray[position].getStatus());
    }

    @Override
    public int getItemCount() {
        return deliveryJobArray.length;
    }
}

