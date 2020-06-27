package com.mobileassignment3.parcel_tracking_app.controllers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobileassignment3.parcel_tracking_app.activities.auth_activities.LoginActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.AdminMainActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.DriverMainActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.ReceiverMainActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Admin;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Customer;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Driver;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FirebaseAuthCustom extends FirebaseController {
    public static List<User> userlist = new ArrayList<>();
    public FirebaseUser getCurrentFirebaseUserObject() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser;
    }
    public FirebaseUser createNewUser(final Activity activity, String email, String password, final int type, final String username) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(activity, "SUCCESS! you can log in now", Toast.LENGTH_LONG).show();
                            setupUserInDatabase(username,user,type);
                            Intent gotoLoginScreen = new Intent(activity, LoginActivity.class);
                            activity.startActivity(gotoLoginScreen);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("ERROR","firebase error can not make new user");
                            if(task.getException().toString().contains("already"))
                                Toast.makeText(activity, "That Email is already in use please try another email", Toast.LENGTH_LONG).show();
                            else Toast.makeText(activity, "Could not sign you up :  "+task.getException(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
        return getCurrentFirebaseUserObject();
    }
    public void updateUser(Object user, String uuid) {
        db.collection("users").document(uuid).set(user);
    }
    private void setupUserInDatabase(String username,FirebaseUser user, int usertype) {
        User parcelAppUser;
        if (usertype == User.DRIVER) {
//
            parcelAppUser = new Driver();
        } else if (usertype == User.RECIEVER) {
            parcelAppUser = new Customer();

        } else {
            parcelAppUser = new Admin();
        }
        parcelAppUser.setType(usertype);
        parcelAppUser.setEmail(getCurrentFirebaseUserObject().getEmail());
        parcelAppUser.setUsername(username);

        db.collection("users").document(getCurrentFirebaseUserObject().getUid()).set(parcelAppUser);
    }
    private void setupUserInDatabase2(String username, int usertype) {
        User parcelAppUser;
        if (usertype == User.DRIVER) {
//
            parcelAppUser = new Driver();
        } else if (usertype == User.RECIEVER) {
            parcelAppUser = new Customer();

        } else {
            parcelAppUser = new Admin();
        }
        parcelAppUser.setType(usertype);
        parcelAppUser.setEmail(getCurrentFirebaseUserObject().getEmail());
        parcelAppUser.setUsername(username);

        db.collection("users").document(getCurrentFirebaseUserObject().getUid()).set(parcelAppUser);
    }
    public void loginUser(final Activity activity , String email, String password) {
        logoutCurrentUser();
        if (email !=null&&!email.equals("")){
            if(password!=null&&!password.equals("")){

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //TODO need to fix get display name
                                    if (user != null){
                                        setParcelAppUser(activity);

                                    }
                                    else Toast.makeText(activity.getApplicationContext(),"Failed - user is null", Toast.LENGTH_LONG).show();
                                    updateUIafterLogin(activity,true);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    if (task.getException().getClass().toString().contains("Credentials"))
                                        Toast.makeText(activity.getApplicationContext(), "Failed to Login: Invalid Credentials", Toast.LENGTH_LONG).show();
                                    else if (task.getException().getClass().toString().contains("TooManyRequest"))
                                        Toast.makeText(activity.getApplicationContext(),"Too many Requests please wait a few seconds before trying again" , Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(activity.getApplicationContext(),task.getException().getClass().toString() , Toast.LENGTH_LONG).show();



                                }
                            }
                        });
            }else{
                Toast.makeText(activity, "Please enter your PASSWORD", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(activity, "Please Enter your EMAIL", Toast.LENGTH_LONG).show();
        }
    }
    public void loginUser(String email, String password, final OnCompleteListener<AuthResult> callback) {
        logoutCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (callback != null) {
                            callback.onComplete(task);
                        }
                    }
                });
    }
    public void getUser(final OnSuccessListener<User> callback) {
        FirebaseUser cu = getCurrentFirebaseUserObject();

        DocumentReference docRef = db.collection("users").document(cu.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (callback != null) {
                    callback.onSuccess(user);
                }
            }
        });
    }
    public void updateUIafterLogin(final Activity activity, boolean loginSuccess) {
        getUser(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getDeliveryJobList().isEmpty()){
                    setupUserInDatabase2(user.getUsername(),user.getTypeArray().get(0));
                }
                try{
                    doIntent(user, activity);

                }catch(Exception e){
                    Toast.makeText(activity, "Still setting you up please login again" +e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void doIntent(User user, Activity activity) {
        Intent myIntent = new Intent(activity, LoginActivity.class);
        if (user.typeArray.get(0) == User.DRIVER) {
            myIntent = new Intent(activity, DriverMainActivity.class);
        } else if (user.typeArray.get(0) == User.RECIEVER) {
            myIntent = new Intent(activity, ReceiverMainActivity.class);
        } else {
            myIntent = new Intent(activity, AdminMainActivity.class);
        }
        activity.startActivity(myIntent);
        activity.finishAffinity();
    }
    public void logoutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
    }

    public void setParcelAppUser(final Activity activity){
        if(userlist!=null)userlist.clear();
        String cuuid = getCurrentFirebaseUserObject().getUid();
        DocumentReference userData = db.collection("users").document(cuuid);
        Task<DocumentSnapshot> userDataGetTask = userData.get();

        userDataGetTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDataDocumentSnapshot = task.getResult();
                    User user = userDataDocumentSnapshot.toObject(User.class);
                    int usertype = user.typeArray.get(0);

                    if (usertype == User.DRIVER) {
                        userlist.add(userDataDocumentSnapshot.toObject(Driver.class));
                    } else if (usertype == User.RECIEVER) {
                        userlist.add(userDataDocumentSnapshot.toObject(Customer.class));
                    } else {
                        userlist.add( userDataDocumentSnapshot.toObject(Admin.class));
                    }
                    parcelAppUserOnCreate(activity);

                }else{
                    Log.d("Firestore Error","reading userdata Failed");
                }
            }
        });
    }

    private void parcelAppUserOnCreate(Activity activity) {
        Toast.makeText(activity.getApplicationContext(),"Welcome! " + getCurrentParcelAppUser().get(0).getUsername(), Toast.LENGTH_LONG).show();

    }

    public List<User> getCurrentParcelAppUser() {

        return  userlist;

    }
}
