package com.mobileassignment3.parcel_tracking_app.activities.auth_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mobileassignment3.parcel_tracking_app.FirebaseController;
import com.mobileassignment3.parcel_tracking_app.SignupActivity;
import com.mobileassignment3.parcel_tracking_app.R;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    TextView usernameEditText;
    TextView passwordEditText;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //new FirebaseController().writeMasterDeliveryJobsToFirestore();
        //will hide the title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //hide the title bar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);
        TextView tvLoginSignup = findViewById(R.id.tvLoginSignup);
        Button btnLogin = findViewById(R.id.btnLogin);
        usernameEditText = findViewById(R.id.etLoginUsername);
        passwordEditText = findViewById(R.id.etLoginPassword);
        tvLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(myIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                loginUserWithEmail(username,password);

            }
        });
        doOnce();



       googleSignin();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

    //main login function;
    private void loginUserWithEmail(String email, String password) {

       new FirebaseController().loginUser(this,email,password);

    }
    //do once
    private void doOnce() {
       // new FirebaseController().createNewUser(this,"receiver2@receiver.com","12345678", User.RECIEVER,"JDoeReceiver");
        //new FirebaseController().createNewUser(this,"driver2@driver.com","12345678",User.DRIVER,"lakshayDriver");

        // new FirebaseController().writeMasterDeliveryJobsToFirestore();
         //new FirebaseController().setDeliveryJobsforAllUsersOnce();


    }

    //google signin code below
    private void startgoogleSignInIntent() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void googleSignin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        startgoogleSignInIntent();
                        break;
                    // ...
                }
            }
        });
    }
    private void handleSignInResult( Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Successfully  Signed In WITH GOOGLE", Toast.LENGTH_SHORT).show();
            new FirebaseController().handleGoogleSignIn(account,this);
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE SIGN IN", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "FAILED TO SIGNIN WITH GOOGLE", Toast.LENGTH_SHORT).show();
        }
    }





}
