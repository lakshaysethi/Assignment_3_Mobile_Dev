package com.mobileassignment3.parcel_tracking_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mobileassignment3.parcel_tracking_app.activities.auth_activities.LoginActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

public class SignupActivity extends AppCompatActivity {

    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //will hide the title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //hide the title bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        // Setup pager




        final Activity signupActivityThis = this;
        Button signupBtn = findViewById(R.id.btnSignup);
        final RadioButton driverRadio = findViewById(R.id.radioButtonDriver);
        final RadioButton receiverRadio = findViewById(R.id.radioButton2);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if driver is selected
                EditText etSignupDriverUsername =  findViewById(R.id.etSignupDriverUsername);
                EditText etSignupDriverPassword1 =  findViewById(R.id.etSignupDriverPassword1);
                EditText etSignupDriverPassword2 =  findViewById(R.id.etSignupDriverPassword2);
                EditText etSignupEmail =  findViewById(R.id.etSignupDriverEmail);

                //else if reciever is selected

                //we dont really need 2 fragments cos there arent many differences - the reciever needs to give his address man

                if (!etSignupDriverPassword1.getText().toString().equals("")&& !etSignupEmail.getText().toString().equals("") && (driverRadio.isChecked()|| receiverRadio.isChecked())) {
                    try{

                        int type;
                        String email = etSignupEmail.getText().toString();
                        String password = etSignupDriverPassword1.getText().toString();
                        String username = etSignupDriverUsername.getText().toString();
                        if(driverRadio.isChecked()){
                            type= User.DRIVER;

                        }else{
                            type = User.RECIEVER;
                        }

                        new FirebaseAuthCustom().createNewUser(signupActivityThis,email,password,type,username);


                    }catch (Exception e) {
                        Toast.makeText(SignupActivity.this, "Error!!!!!! create new user in FirebaseController", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


    }


}





