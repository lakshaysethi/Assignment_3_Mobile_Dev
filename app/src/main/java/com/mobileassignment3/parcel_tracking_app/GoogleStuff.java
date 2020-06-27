package com.mobileassignment3.parcel_tracking_app;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

public  class GoogleStuff extends FirebaseController{

    public void handleGoogleSignIn(GoogleSignInAccount account, Activity activity) {
        try{

            firebaseAuthWithGoogle(account.getIdToken());
            FirebaseUser cu = mAuth.getCurrentUser();

            if(cu!=null){
                Toast.makeText(activity, "Welcome!"+ cu.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
            updateUIafterLogin_danica(activity,true);



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


}
