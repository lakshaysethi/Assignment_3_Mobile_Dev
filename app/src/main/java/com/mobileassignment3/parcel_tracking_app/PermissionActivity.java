package com.mobileassignment3.parcel_tracking_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.mobileassignment3.parcel_tracking_app.activities.auth_activities.LoginActivity;

public class PermissionActivity extends AppCompatActivity {
    public static final int ACCESS_FINE_LOCATION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //will hide the title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //hide the title bar
        getSupportActionBar().hide();

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, this.ACCESS_FINE_LOCATION_CODE)) {
            FirebaseController controller = new FirebaseController();
            FirebaseUser currentUser = controller.getCurrentFirebaseUserObject();
            if (currentUser == null) { // Not logged in, go to LoginActivity
                Intent myIntent = new Intent(PermissionActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            } else { // Login session still valid, go to activity according to user role
                controller.updateUIafterLogin(this, true);
            }
        }

        setContentView(R.layout.activity_permission);
    }


    // Function to check and request permission.
    public boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast pToast = Toast.makeText(
                        this,
                        "Permission Granted",
                        Toast.LENGTH_LONG);
                pToast.show();

                Intent myIntent = new Intent(PermissionActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            } else {
                Toast.makeText(this,
                        "Permission Denied, Closing the App",
                        Toast.LENGTH_LONG)
                        .show();
                this.finish();
            }
        }
    }
}
