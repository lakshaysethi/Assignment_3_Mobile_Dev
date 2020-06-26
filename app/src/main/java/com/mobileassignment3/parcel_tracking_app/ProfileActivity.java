package com.mobileassignment3.parcel_tracking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.mobileassignment3.parcel_tracking_app.activities.auth_activities.LoginActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        Button logOutBtn = findViewById(R.id.btnLogout);
        TextView emailTextView = findViewById(R.id.tvMyAccountEmail);
        final TextView usernameTextView = findViewById(R.id.tvMyAccountUsername);

        new FirebaseController().getUser(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                usernameTextView.setText(user.getUsername());
            }
        });

        FirebaseUser currentuser = new FirebaseController().getCurrentFirebaseUserObject();

        try{
            String emailOfCurrentUser = currentuser.getEmail();
            emailTextView.setText(emailOfCurrentUser);
        }catch (Exception e){
            if(currentuser==null){
                Toast.makeText(this, "Authentication error- please login again", Toast.LENGTH_SHORT).show();
                gotoLoginScreen();
            }
            Toast.makeText(ProfileActivity.this, "Error Setting Tvs"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    new FirebaseController().logoutCurrentUser();
                    Toast.makeText(ProfileActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                    gotoLoginScreen();

                }catch (Exception e){
                    Toast.makeText(ProfileActivity.this, "Error LoggingOut"+e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void gotoLoginScreen() {

        Intent gotoLoginScreen = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(gotoLoginScreen);
        finishAffinity();
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
