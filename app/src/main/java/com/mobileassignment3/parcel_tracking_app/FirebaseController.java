package com.mobileassignment3.parcel_tracking_app;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;


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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileassignment3.parcel_tracking_app.activities.auth_activities.LoginActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.AdminMainActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.DriverMainActivity;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.MainActivityForAllUsers;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.ReceiverMainActivity;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.Parcel;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Admin;
import com.mobileassignment3.parcel_tracking_app.model_classes.ParcelMessage;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Customer;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Driver;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class FirebaseController {
    public FirebaseAuth mAuth;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    QueryDocumentSnapshot theDocument;
    ArrayList <User> allUsers = new ArrayList<User>();
    private Object userData;
    private List<DeliveryJob> Djal;

    // Initialize Firebase Auth
    public FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
      //makeAdminUser();
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
                    }
                });
    }


//    private void  makeAdminUser(){
//        createNewUser("admin@parcel.com","12345678",User.ADMIN,"admin");
//    }

    public List<DeliveryJob> writeMasterDeliveryJobsToFirestore(){

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


        //Get the delivery jobs document which contains all delivery items
        DocumentReference deliveryJobsDocumentRef = db.collection("masterDeliveryJobs").document("deliveryJobsDocument");
        //Add the newly created delivery jobs to the masterList
        deliveryJobsDocumentRef
                .set(masterDeliveryJobs)
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
        //deliveryJobArrayList;
       // writedeliveryJobsToUser(deliveryJobArrayList,"3XhbnMbM9UT9TvcuC3KvROfR4Q03",User.ADMIN);
        return deliveryJobArrayList;

    }

    public void assignParcelToDriver(final String driverUserName, ArrayList<DeliveryJob> trackingNumbers){
        //TODO Get which parcels the admin has selected, and use their tracking numbers
        Log.d("JOBS","assignParcelToDriver: "+ trackingNumbers.toString());
        //For each tracking number, assign driver.
        for (DeliveryJob jobIterator : trackingNumbers){
            //Set the tracking number for the job we want to update
            final String trackingNumber = jobIterator.getTrackingNumber();
            //Get the current list of delivery jobs
            try{
                db.collection("masterDeliveryJobs")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            //Do whatever
                                            //                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                            DocumentSnapshot document  = task.getResult().getDocuments().get(0);
        //                                    Log.d("FIREBASE", document.getId() + " => " + document.getData());
                                            if(document.contains("masterList")){
                                                document.get("masterList");

                                                Djal = document.toObject(MasterListDocument.class).masterList;
                                                for (DeliveryJob deliveryJob : Djal) {
                                                    if (deliveryJob.getTrackingNumber().equals(trackingNumber)){                    //Find the delivery job you want to update
                                                        for (User thisUser : allUsers) {
                                                            if (thisUser.getUsername().equals(driverUserName)){                     //Find the driver object you want to assign to the job
                                                                Driver driverToAssign = (Driver)thisUser;
                                                                deliveryJob.setAssignedDriver(driverToAssign);                      //and assign the entered driver to it
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        Log.d("Firebase error", "Entered driver not found");
                                                    }
                                                }
                                            }
                                        }
                                    }).start();


                                } else {
                                    Log.w("Firebase error", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }catch (Exception e){
                Log.w("Firebase error", "Error getting documents.");
            }
        }
        updateMasterDeliveryJobList(Djal);                                                                                  //update the masterDeliveryJobList
    }

    private void updateMasterDeliveryJobList(List<DeliveryJob> updatedDeliveryJobList) {
        //This function will take a list of delivery jobs
        //And overwrite the existing devieryJobsDocument
        db.collection("masterDeliveryJobs").document("deliveryJobsDocument")
                .update("masterList", updatedDeliveryJobList);
    }

    public void getAllUsers(){
        try {
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //put the UUId of the user and the user data into the allUsers hashmap
                                Driver tempDriver = new Driver();
                                tempDriver = document.toObject(Driver.class);
                                allUsers.add(tempDriver);
                            }
                            Log.d("Temp", allUsers.toString());
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        }
                    });
        } catch (Exception e) {
            Log.w("Firebase error", "Error getting documents.");
        }
    }

     public void setDeliveryJobsDocumentData(Map data) {
     //Get the delivery jobs document which contains all delivery items
     DocumentReference deliveryJobsDocumentRef = db.collection("masterDeliveryJobs").document("deliveryJobsDocument");
     //set the data to a map that's passed into this function
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
     }


    public void writedeliveryJobsToUser(ArrayList<DeliveryJob> deliveryJobArrayList, final String uuid, final int userType){

//     //Assign a delivery job list to a driver
//     public void writedeliveryJobsToDriver(   ArrayList<DeliveryJob> deliveryJobArrayList){

        final ArrayList<DeliveryJob> djal = deliveryJobArrayList;
        db.collection("users").document(uuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();



                    if (userType == User.DRIVER) {
//            user = (Driver)user;
                       Driver parcelappuser =    doc.toObject(Driver.class);
                        parcelappuser.setDeliveryJobList(djal);

                        updateUser(parcelappuser,uuid);
                        db.collection("users").document(uuid).set(parcelappuser);

                    } else if (userType == User.RECIEVER) {
                      Customer  parcelappuser    =  doc.toObject(Customer.class);
                        parcelappuser.setDeliveryJobList(djal);

                        updateUser(parcelappuser,uuid);
                        db.collection("users").document(uuid).set(parcelappuser);

                    } else {
                      Admin  parcelappuser   =  doc.toObject(Admin.class);
                        parcelappuser.setDeliveryJobList(djal);

                        updateUser(parcelappuser,uuid);
                        //db.collection("users").document(uuid).set(parcelappuser);
                    }
                }else{
                    Log.d("Error","Firebasecontroller error");
                }
            }
        });

    }

    public void updateUser(Object user, String uuid) {
        db.collection("users").document(uuid).set(user);
    }

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
                           //TODO remove this
                                writedeliveryJobsToUser((ArrayList<DeliveryJob>)writeMasterDeliveryJobsToFirestore(),getCurrentFirebaseUserObject().getUid(),User.RECIEVER);



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
        return getCurrentFirebaseUserObject();
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
                                    if (user != null)
                                        Toast.makeText(activity.getApplicationContext(),
                                                "Welcome! "+ user.getEmail(), Toast.LENGTH_LONG).show();
                                    else Toast.makeText(activity.getApplicationContext(),
                                            "Failed - user is null", Toast.LENGTH_LONG).show();
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
            db.collection("masterDeliveryJobs")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        private ArrayList<DeliveryJob> DjAl;

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   // Log.d("FIREBASE", document.getId() + " => " + document.getData());
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

/*
//Usage of getCurrentParcelTrackerUser function:
//
//User cu = getCurrentParcelTrackerUser(null,"username as set on signup");
//User cu = getCurrentParcelTrackerUser(null,"usertype Int as String");
//
//* */
//    public User getCurrentParcelTrackerUser(User user, final  String cuuid){
//
//        if (user != null   ){
//            DocumentReference userData = db.collection("users").document(cuuid);
//            Task<DocumentSnapshot> udataGetTask = userData.get();
//
//            udataGetTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()){
//                        DocumentSnapshot userDataDocumentSnapshot = task.getResult();
//                        User currentUser = userDataDocumentSnapshot.toObject(User.class);
//                        getCurrentParcelTrackerUser(currentUser,cuuid);
//                    }
//                }
//            });
//            return user;
//
//        }
////        try {
////            TimeUnit.MILLISECONDS.sleep(400);
////        } catch (InterruptedException e) {
////            Log.d("SLeep error","Sleep Error");
////            e.printStackTrace();
////        }
//        //TODO test above code later - it cloud work by not hanginig the entire application/ im concerend abot the task above
//        return getCurrentParcelTrackerUser(user,cuuid);
//
//    }
//
////TODO #5
//    public List<DeliveryJob> getdeliveryJobsAssociatedCurrentUser() {
//        String cuuid = getCurrentFirebaseUserObject().getUid();
//        User user = getCurrentParcelTrackerUser(null,cuuid);
//        ArrayList<DeliveryJob> djal = new ArrayList<DeliveryJob>();
//
//        int usertype = user.getPrimaryType();
//        if (usertype == User.DRIVER) {
////            user = (Driver)user;
//            return  ((Driver) user).getDeliveryJobList();
//        } else if (usertype == User.RECIEVER) {
//           return ( (Customer)user).getDeliveryJobList();
//
//        } else {
//          return  ((Admin)user).getDeliveryJobList();
//        }
//
////TODO convert above copied code to cunction the if switch
//    }

    public void setArraylistInAdapterOfActivity(final RecyclerView rvParcel, final MainActivityForAllUsers MainActivity) {

        String cuuid = getCurrentFirebaseUserObject().getUid();
        DocumentReference userData = db.collection("users").document(cuuid);
        Task<DocumentSnapshot> udataGetTask = userData.get();
        final List<DeliveryJob>[] djal = new List[]{new ArrayList<DeliveryJob>()};
        udataGetTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDataDocumentSnapshot = task.getResult();


                    User user = userDataDocumentSnapshot.toObject(User.class);
                    int usertype = user.typeArray.get(0);
                    if (usertype == User.DRIVER) {
//            user = (Driver)user;
                        djal[0] = userDataDocumentSnapshot.toObject(Driver.class).getDeliveryJobList();
                    } else if (usertype == User.RECIEVER) {
                        djal[0] = userDataDocumentSnapshot.toObject(Customer.class).getDeliveryJobList();

                    } else {
                        djal[0] = userDataDocumentSnapshot.toObject(Admin.class).getDeliveryJobList();
                    }
                    MainActivity.setArraylistInAdapter(rvParcel, (ArrayList<DeliveryJob>) djal[0]);
                }
            }
        });

    }


    public void sendMessageToReceiver(final String title, final String message, final String receiverEmail, final OnSuccessListener listener, final OnFailureListener failureListener) {
        getUser(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                ParcelMessage data = new ParcelMessage(title, message, user.getEmail(), receiverEmail, (new Date()).getTime());
                db.collection("messages").document(receiverEmail)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                if (listener != null) {
                                    listener.onSuccess(aVoid);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                if (failureListener != null) {
                                    failureListener.onFailure(e);
                                }
                            }
                        });
            }
        });

    }

    public void listenToMessage(String receiverEmail, final long lastReceivedTimestamp, final OnSuccessListener<ParcelMessage> listener) {
        final DocumentReference docRef = db.collection("messages").document(receiverEmail);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    if (listener != null) {
                        ParcelMessage message = snapshot.toObject(ParcelMessage.class);
                        if (message.timestamp >= lastReceivedTimestamp) {
                            listener.onSuccess(message);
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
}
