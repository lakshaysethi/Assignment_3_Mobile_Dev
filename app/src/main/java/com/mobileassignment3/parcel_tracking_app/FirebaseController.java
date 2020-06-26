package com.mobileassignment3.parcel_tracking_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileassignment3.parcel_tracking_app.activities.auth_activities.LoginActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.AdminMainActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.DriverMainActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.ReceiverMainActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Customer;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Driver;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class FirebaseController {
    public FirebaseAuth mAuth;
   public  FirebaseFirestore db = FirebaseFirestore.getInstance();
    QueryDocumentSnapshot theDocument;

    // Initialize Firebase Auth
    public FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
//        makeAdminUser();
    }


    public void handleGoogleSignIn(GoogleSignInAccount account,Activity activity) {
        try{

            firebaseAuthWithGoogle(account.getIdToken());
            FirebaseUser cu = mAuth.getCurrentUser();

            if(cu!=null){
                Toast.makeText(activity, "Welcome!"+ cu.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
            updateUIafterLogin(activity,true);

      

        }catch (Exception e){
            Toast.makeText(activity, account.getDisplayName(), Toast.LENGTH_SHORT).show();
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }


//    private void  makeAdminUser(){
//        createNewUser("admin@parcel.com","12345678",User.ADMIN,"admin");
//    }

    public void writeMasterDeliveryJobsToFirestore(){

        ArrayList<DeliveryJob> deliveryJobArrayList = new ArrayList<DeliveryJob>();
        String[] senders = {"Danica", "Lakhsay", "John Casey", "Raza", "Obama", "Paul Bartlett", "Dila"};
        String[] packages = {"Letter", "Laptop", "Jacket", "Certificate", "Backpack", "Payslip", "Vaccine" };
        //Writing 7 random delivery jobs to a temp delivery job array list
        for(int i=0;i<7;i++) {
            Random rand1 = new Random();
            Random rand2 = new Random();
            int n = rand1.nextInt(7);
            int m = rand2.nextInt(7);

            DeliveryJob nDJ = new DeliveryJob();
            nDJ.addParcel(new Parcel( packages[n] + " from " + senders[m]));
//            Customer customer = new Customer();
//            db.collection("users").document("S6GVxjjGlwhiNoxQfAOJ6Q08S4Z2").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                }
//            });
//            nDJ.setReceiver();
            deliveryJobArrayList.add(nDJ);
        }

        Map<String, Object> masterDeliveryJobs = new HashMap<>();
        //Putting the delivery job array list into a hashmap
        masterDeliveryJobs.put("masterList", deliveryJobArrayList);

        setDeliveryJobsDocumentData(masterDeliveryJobs);
    }

    public void assignParcelToDriver(final String driverUserName){
        //TODO Get which parcels the admin has selected, and use their tracking numbers

        final String trackingNumber = "3f74af75-5fcd-40ec-a583-031b45c7106b";
        //drivertwo
        //Get the current list of delivery jobs

        try{
            db.collection("masterDeliveryJobs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FIREBASE", document.getId() + " => " + document.getData());
                                    if(document.contains("masterList")){
                                        document.get("masterList");
                                        List<DeliveryJob> Djal = document.toObject(MasterListDocument.class).masterList;
                                        //Find the delivery job you want to update and update it
                                        for (DeliveryJob deliveryJob : Djal) {
                                            if (deliveryJob.getTrackingNumber().equals(trackingNumber)){
                                                //TODO INSTEAD OF CREATING A NEW DRIVER, get the list of drivers
                                                //and assign this to that driver object
                                                Driver temp = new Driver();
                                                temp.setUsername(driverUserName);
                                                deliveryJob.setAssignedDriver(temp);
                                            }
                                        }
                                        Map<String, Object> masterDeliveryJobs = new HashMap<>();
                                        //Putting the delivery job array list into a hashmap
                                        masterDeliveryJobs.put("masterList", Djal);
                                        setDeliveryJobsDocumentData(masterDeliveryJobs);
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
    }

    public void setDeliveryJobsDocumentData(Map data) {
        //Get the delivery jobs document which contains all delivery items
        DocumentReference deliveryJobsDocumentRef = db.collection("masterDeliveryJobs").document("deliveryJobsDocument");
        //Add the newly created delivery jobs to the masterList
        deliveryJobsDocumentRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FIREBASE", "Data successfully added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FIREBASE", "Error updating document", e);
                    }
                });

        writedeliveryJobsToDriver( deliveryJobArrayList);
    }
    public void writedeliveryJobsToDriver(   ArrayList<DeliveryJob> deliveryJobArrayList){

        final ArrayList<DeliveryJob> djal = deliveryJobArrayList;
        db.collection("users").document("vVPfYGhf5nex005yGBnkikIoZrI3").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Driver driver = doc.toObject(Driver.class);
                    driver.setDeliveryJobList(djal);
                    updateDriver(driver);
                }else{
                    Log.d("Error","Firebasecontroller error");
                }
            }
        });

    }

    public void updateDriver(Driver driver) {
        db.collection("users").document("vVPfYGhf5nex005yGBnkikIoZrI3").set(driver);
    }

    public FirebaseUser getCurrentUser() {
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
        return getCurrentUser();
    }

    public FirebaseUser createNewUser( String email, String password, final int type, final String username) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            setupUserInDatabase(username,user,type);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("ERROR","firebase error can not make new user");
                        }

                    }
                });
        return getCurrentUser();
    }

    private void setupUserInDatabase(String username,FirebaseUser user, int type) {
        User parcelAppUser = new User();
        parcelAppUser.setType(type);
        parcelAppUser.setEmail(getCurrentUser().getEmail());
        parcelAppUser.setUsername(username);

        db.collection("users").document(getCurrentUser().getUid()).set(parcelAppUser);
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

    public void loginUserAndUpdateUI(final Activity activity, String email, String password) {
        if (email != null && !email.equals("")) {
            if (password != null && !password.equals("")) {
                loginUser(email, password, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null)
                                Toast.makeText(activity.getApplicationContext(),
                                        "Welcome! " + user.getEmail(), Toast.LENGTH_LONG).show();
                            else Toast.makeText(activity.getApplicationContext(),
                                    "Failed - user is null", Toast.LENGTH_LONG).show();
                            updateUIafterLogin(activity, true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            if (task.getException().getClass().toString().contains("Credentials"))
                                Toast.makeText(activity.getApplicationContext(), "Failed to Login: Invalid Credentials", Toast.LENGTH_LONG).show();
                            else if (task.getException().getClass().toString().contains("TooManyRequest"))
                                Toast.makeText(activity.getApplicationContext(), "Too many Requests please wait a few seconds before trying again", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(activity.getApplicationContext(), task.getException().getClass().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(activity, "Please enter your PASSWORD", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "Please Enter your EMAIL", Toast.LENGTH_LONG).show();
        }
    }

    public void getUser(final OnSuccessListener<User> callback) {
        FirebaseUser cu = getCurrentUser();

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
                doIntent(user, activity);
            }
        });
    }

    private void doIntent(User user, Activity activity) {
        Intent myIntent = new Intent(activity, LoginActivity.class);
        if (user.getPrimaryType() == User.DRIVER) {
            myIntent = new Intent(activity, DriverMainActivity.class);
        } else if (user.getPrimaryType() == User.RECIEVER) {
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


    public void getListOfCustomers() {
        Task<QuerySnapshot> task = db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        List<Customer> custList = new ArrayList<Customer>();
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot userDocument : task.getResult()) {
                        int userType = (Integer) userDocument.get("primaryType");
                        if (userType == User.RECIEVER) {
                            Customer cust = userDocument.toObject(Customer.class);
                            custList.add(cust);
                            setDeliveryJobsforAllUsersOnce(custList);
                        }

                    }

                }
            }

        });
    }

    private void setDeliveryJobsforAllUsersOnce(final List<Customer> custList) {
        try{
            new FirebaseController().db.collection("masterDeliveryJobs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                        private ArrayList<DeliveryJob> DjAl;

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FIREBASE", document.getId() + " => " + document.getData());
                                    if(document.contains("masterList")){
                                        document.get("masterList");
                                        List<DeliveryJob> Djl = document.toObject(MasterListDocument.class).masterList;
                                        DjAl = (ArrayList<DeliveryJob>)Djl;
                                        int i =0;
                                        for(Customer cust :custList){
                                           //get random DeliveryJobs from Djal
                                             //cust.;
                                            //TODO -- im working here (lakshay) setDeliveryJobsforAllUsersOnce();
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
    }
}
