package com.mobileassignment3.parcel_tracking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseAuthCustom;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

public class SignupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);
        final Activity signupActivityThis = this;
        Button signupBtn = findViewById(R.id.btnSignup);
        final RadioButton driverRadio = findViewById(R.id.radioButtonDriver);
        final RadioButton receiverRadio = findViewById(R.id.radioButton2);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etSignupDriverUsername =  findViewById(R.id.etSignupDriverUsername);
                EditText etSignupDriverPassword1 =  findViewById(R.id.etSignupDriverPassword1);
                EditText etSignupDriverPassword2 =  findViewById(R.id.etSignupDriverPassword2);
                EditText etSignupEmail =  findViewById(R.id.etSignupDriverEmail);


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
                        if(!username.contains("dmin")){
                            new FirebaseAuthCustom().createNewUserWithEmail(signupActivityThis,email,password,type,username);
                        }else{
                            new FirebaseAuthCustom().makeAdminUser(signupActivityThis,email,password,username);
                        }


                    }catch (Exception e) {
                        Toast.makeText(SignupActivity.this, "Error!!!!!! create new user in OldFirebaseController", Toast.LENGTH_LONG).show();
                        Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


    }


}





