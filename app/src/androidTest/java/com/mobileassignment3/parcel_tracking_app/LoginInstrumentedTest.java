package com.mobileassignment3.parcel_tracking_app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.mobileassignment3.parcel_tracking_app.model_classes.user.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {


    private static final String EMAIL = "driver@driver.com";
    private static final String PASSWORD = "12345678";
    private FirebaseController firebaseController;
    private Context appContext;

    @Before
    public void setup() {
        firebaseController = new FirebaseController();
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void useAppContext() {
        assertEquals("com.mobileassignment3.parcel_tracking_app", appContext.getPackageName());
    }

    @Test
    public void loginTest() {
        firebaseController.loginUser(EMAIL, PASSWORD, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                assertEquals(true, task.isSuccessful());

                // Should have user after logged in
                FirebaseUser user = firebaseController.getCurrentFirebaseUserObject();
                assertNotNull(user);
                assertEquals(EMAIL, user.getEmail());

                firebaseController.getUser(new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        // Check user role type
                        assertEquals(User.DRIVER, user.getPrimaryType());
                    }
                });

            }
        });
    }
}
