package com.mobileassignment3.parcel_tracking_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView rvNotification;
    private RecyclerView.Adapter adapterNotification;
    private RecyclerView.LayoutManager layoutManagerNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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

        rvNotification = findViewById(R.id.rvNotification);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvNotification.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerNotification = new LinearLayoutManager(this);
        rvNotification.setLayoutManager(layoutManagerNotification);

        // specify an adapter
        Notification[] myDataset = new Notification[] {
                new Notification("Notification 1", "Beef inside"),
                new Notification("Notification 2", "Countdown delivery"),
                new Notification("Notification 3", "Dahua supermarket tuan gou"),
                new Notification("Notification 4", "Beef inside"),
                new Notification("Notification 5", "Countdown delivery"),
                new Notification("Notification 6", "Dahua supermarket tuan gou"),
                new Notification("Notification 7", "Beef inside"),
                new Notification("Notification 8", "Countdown delivery"),
                new Notification("Notification 9", "Dahua supermarket tuan gou"),
        };
        adapterNotification = new NotificationAdapter(myDataset);
        rvNotification.setAdapter(adapterNotification);
    }

    // implemented the menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }
    // implemented the menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.backArrow:
            finish();
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }

}

class Notification {
    public final String title;
    public final String detail;

    Notification(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }
}

class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Notification[] mDataset;

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
    public NotificationAdapter(Notification[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_notification, parent, false);
        TextView title = (TextView) v.findViewById(R.id.cardNotificationTitle);
        TextView detail = (TextView) v.findViewById(R.id.cardNotificationDetail);

        MyViewHolder vh = new MyViewHolder(v, title, detail);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textViewTitle.setText(mDataset[position].title);
        holder.textViewDetail.setText(mDataset[position].detail);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}