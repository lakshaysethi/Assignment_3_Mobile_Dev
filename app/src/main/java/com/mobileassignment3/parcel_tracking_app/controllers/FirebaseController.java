package com.mobileassignment3.parcel_tracking_app.controllers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobileassignment3.parcel_tracking_app.activities.main_activities.MainActivityForAllUsers;
import com.mobileassignment3.parcel_tracking_app.controllers.FirebaseAuthCustom;
import com.mobileassignment3.parcel_tracking_app.model_classes.DeliveryJob;
import com.mobileassignment3.parcel_tracking_app.model_classes.ParcelMessage;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Admin;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Customer;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.Driver;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FirebaseController {
    public FirebaseAuth mAuth;
   public  FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Initialize Firebase Auth
    public FirebaseController() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void setArraylistInAdapterOfActivity(final RecyclerView rvParcel, final MainActivityForAllUsers MainActivity) {

        String cuuid = new FirebaseAuthCustom().getCurrentFirebaseUserObject().getUid();
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
        new FirebaseAuthCustom().getUser(new OnSuccessListener<User>() {
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


    public void sendMessageToReceiver_lakshay(final String title, final String message, final String receiverEmail, final OnSuccessListener listener, final OnFailureListener failureListener) {
        FirebaseUser user = new FirebaseAuthCustom().getCurrentFirebaseUserObject();
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

  /*  public void getUserLakshay_s_function(final OnSuccessListener<User> callback) {
        FirebaseUser FBcu = getCurrentFirebaseUserObject();

        db.collection("users").document(FBcu.getUid()).get();
        // User user = documentSnapshot.toObject(User.class);
        if (callback != null) {
            //  callback.onSuccess(user);
        }
    }

    public void updateUIafterLogin_lakshay(final Activity activity, boolean loginSuccess) {
        FirebaseUser cu = getCurrentFirebaseUserObject();

        DocumentReference userDocRef = db.collection("users").document(cu.getUid());
        Task<DocumentSnapshot> userTask = userDocRef.get();
        userTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    User user = task.getResult().toObject(User.class);
//                    setupUserInDatabase2(user.getUsername(), user.getTypeArray().get(0));
                    if (user.getDeliveryJobList().isEmpty()) {
                        setupUserInDatabase2(user.getUsername(), user.getTypeArray().get(0));
                    }
                    try {
                        doIntent(user, activity);

                    } catch (Exception e) {
                        Toast.makeText(activity, "Still setting you up please login again" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }*/
/*

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

*/

/*


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

*/

/*

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
*/

/*
//Usage of getCurrentParcelTrackerUser function:
//
//User cu = getCurrentParcelTrackerUser(null,"username as set on signup");
//User cu = getCurrentParcelTrackerUser(null,"usertype Int as String");
//
//* */

/*
    public User getCurrentParcelTrackerUser(User user, final  String cuuid){

        if (user != null   ){
            DocumentReference userData = db.collection("users").document(cuuid);
            Task<DocumentSnapshot> udataGetTask = userData.get();

            udataGetTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot userDataDocumentSnapshot = task.getResult();
                        User currentUser = userDataDocumentSnapshot.toObject(User.class);
                        getCurrentParcelTrackerUser(currentUser,cuuid);
                    }
                }
            });
            return user;

        }
//        try {
//            TimeUnit.MILLISECONDS.sleep(400);
//        } catch (InterruptedException e) {
//            Log.d("SLeep error","Sleep Error");
//            e.printStackTrace();
//        }
        //TODO test above code later - it cloud work by not hanginig the entire application/ im concerend abot the task above
        return getCurrentParcelTrackerUser(user,cuuid);

    }

//TODO #5
    public List<DeliveryJob> getdeliveryJobsAssociatedCurrentUser() {
        String cuuid = getCurrentFirebaseUserObject().getUid();
        User user = getCurrentParcelTrackerUser(null,cuuid);
        ArrayList<DeliveryJob> djal = new ArrayList<DeliveryJob>();

        int usertype = user.getPrimaryType();
        if (usertype == User.DRIVER) {
//            user = (Driver)user;
            return  ((Driver) user).getDeliveryJobList();
        } else if (usertype == User.RECIEVER) {
           return ( (Customer)user).getDeliveryJobList();

        } else {
          return  ((Admin)user).getDeliveryJobList();
        }

//TODO convert above copied code to cunction the if switch
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
                            updateUIafterLogin_danica(activity, true);
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
*/

/*
    private void  makeAdminUser(){
        createNewUser("admin@parcel.com","12345678",User.ADMIN,"admin");
    }*/

/*

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
*/



}

