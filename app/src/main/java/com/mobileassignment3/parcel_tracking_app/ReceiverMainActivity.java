package com.mobileassignment3.parcel_tracking_app;

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
import android.widget.Toolbar;

import com.mobileassignment3.parcel_tracking_app.classes.Parcel;
import java.util.ArrayList;
public class ReceiverMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receiver_main);

        // Change the actionbar title and icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_person_pin_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Receiver name");

        // Click the action bar title to open the profile activity
        findViewById(R.id.action_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ReceiverMainActivity.this, ProfileActivity.class);
                startActivity(myIntent);
            }
        });


        RecyclerView rvParcel = findViewById(R.id.rvMyParcel);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvParcel.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManagerParcel = new LinearLayoutManager(this);
        rvParcel.setLayoutManager(layoutManagerParcel);

        // specify an adapter
        ArrayList<Parcel> myDataset = new ArrayList<Parcel>();
        RecyclerView.Adapter adapterParcel = new ParcelAdapter(myDataset);
        rvParcel.setAdapter(adapterParcel);
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
            Intent myIntent = new Intent(ReceiverMainActivity.this, NotificationActivity.class);
            startActivity(myIntent);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }
}

class ParcelAdapter extends RecyclerView.Adapter<ParcelAdapter.MyViewHolder> {
    private ArrayList<Parcel> mDataset;

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
    public ParcelAdapter(ArrayList<Parcel> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ParcelAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.textViewTitle.setText(mDataset.get(position).getDescription());
        holder.textViewDetail.setText(mDataset.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }




}
