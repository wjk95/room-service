package com.rs.roomservice.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rs.roomservice.R;
import com.rs.roomservice.ScannerActivity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    private CardView mWorkerLoginCardView;
    private CardView mClientLoginCardView;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private static int RC_SIGN_IN = 100;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;



    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



        mWorkerLoginCardView = findViewById(R.id.card_worker);
        mClientLoginCardView = findViewById(R.id.card_client);




        mWorkerLoginCardView.setOnClickListener((View view) -> {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);


        });


        mClientLoginCardView.setOnClickListener((View view) -> {

            Intent intent = new Intent(this, ScannerActivity.class);
            intent.putExtra(ScannerActivity.AutoFocus, true);
            intent.putExtra(ScannerActivity.UseFlash, false);
            intent.putExtra(ScannerActivity.AutoCapture, false);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);


            });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(ScannerActivity.BarcodeObject);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    String scannedCode = barcode.displayValue;
                    clientLogin(scannedCode);

                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        if (requestCode == RC_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            List<String> hotelList = new ArrayList<>();
            db.collection("hotels").get().addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        hotelList.add(document.getId());
                    }
                    Log.d(TAG, hotelList.toString());

                    for (String s : hotelList) {
                        DocumentReference docRef = db.collection("hotels").document(s).collection("users").document(uid);
                        docRef.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> task2) -> {
                            DocumentSnapshot snapshot = task2.getResult();
                            if (snapshot.exists()) {
                                String workerType = snapshot.getString("type");

                                if (resultCode == RESULT_OK && workerType.equals("manager")) {
                                    // Successfully signed in
                                    Intent intent = new Intent(this, ManagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (resultCode == RESULT_OK && workerType.equals("maid")) {

                                    Intent intent = new Intent(this, MaidActivity.class);
                                    startActivity(intent);
                                    finish();


                                } else if (resultCode == RESULT_OK && workerType.equals("consierge")) {

                                    Intent intent = new Intent(this, ConsiergeActivity.class);
                                    startActivity(intent);
                                    finish();


                                } else if (resultCode == RESULT_OK && workerType.equals("receptionist")) {

                                    Intent intent = new Intent(this, ReceptionistActivity.class);
                                    startActivity(intent);
                                    finish();


                                } else {
                                    // Sign in failed. If response is null the user canceled the
                                    // sign-in flow using the back button. Otherwise check
                                    // response.getError().getErrorCode() and handle the error.
                                    // ...
                                }
                            }

                        });
                    }

                }


            });
        }
    }

    public void clientLogin(String code) {

        List<String> hotelList = new ArrayList<>();
        db.collection("hotels").get().addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            hotelList.add(document.getId());
                        }
                        Log.d(TAG, hotelList.toString());

                        for(String s : hotelList) {
                            DocumentReference docRef = db.collection("hotels").document(s)
                                    .collection("rooms").document(code);

                            docRef.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> documentSnapshotTask) -> {

                                if (documentSnapshotTask.isSuccessful()) {
                                    DocumentSnapshot document = documentSnapshotTask.getResult();
                                    if (document.exists()) {

                                        mAuth.signInAnonymously()
                                                .addOnCompleteListener(LoginActivity.this, (@NonNull Task<AuthResult> authResultTask) -> {

                                                    if(authResultTask.isSuccessful()) {
                                                        docRef.update("roomOccupied", true);

                                                        String userId = mAuth.getUid();
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("clientId", userId);
                                                        user.put("roomId", code);

                                                        docRef.collection("clients").document().set(user);

                                                        String roomNumber = document.getString("roomNumber");

                                                        Intent intent = new Intent(LoginActivity.this, ClientActivity.class);
                                                        intent.putExtra("roomNumber", roomNumber);
                                                        intent.putExtra("hotelName", s);
                                                        intent.putExtra("roomId", code);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                        mAuth.signOut();
                                                    }
                                                });
                                    } else {

                                        Toast.makeText(LoginActivity.this, "Authentication failed. Wrong ID", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }


        });


    }




}



