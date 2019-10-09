package com.rs.roomservice.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rs.roomservice.R;

import java.util.HashMap;
import java.util.Map;

public class ClientActivity extends AppCompatActivity {


    private Button logout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private CardView mCleaningCardView;
    private CardView mTaxiCardView;
    private CardView mStaffCardView;
    private CardView mHotelCardView;

    private TextView mUserIdTextView;
    private TextView mRoomNumberTextView;




    private String roomNumber;
    private String hotelName;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Intent getRoomInfoIntent = getIntent();

        Bundle extras = getRoomInfoIntent.getExtras();
        if(extras != null) {
            roomNumber = extras.getString("roomNumber");
            hotelName = extras.getString("hotelName");
            roomId = extras.getString("roomId");
        }
        logout = findViewById(R.id.btn_client_logout);

        mCleaningCardView = findViewById(R.id.card_cleaning_service);
        mTaxiCardView = findViewById(R.id.card_taxi_order);
        mStaffCardView = findViewById(R.id.card_call_staff);
        mHotelCardView = findViewById(R.id.card_hotel_info);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        mRoomNumberTextView = findViewById(R.id.tv_user_room_number);
        if(roomNumber != null)
            mRoomNumberTextView.setText("Your room: " + roomNumber);

        logout.setOnClickListener((View v) -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(ClientActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });





        mHotelCardView.setOnClickListener((View v) -> {
            Intent intent  = new Intent(ClientActivity.this, HotelInfoActivity.class);
            intent.putExtra("hotelName", hotelName);
            startActivity(intent);
        });

        mTaxiCardView.setOnClickListener((View v) -> {

            Intent intent  = new Intent(ClientActivity.this, TaxiActivity.class);

            if(roomNumber != null) {
                intent.putExtra("roomNumber", roomNumber);
                intent.putExtra("hotelName", hotelName);
            }
            startActivity(intent);

        });

        mCleaningCardView.setOnClickListener((View v) -> {
            Intent intent  = new Intent(ClientActivity.this, CleaningActivity.class);
            if(roomNumber != null)
                intent.putExtra("roomNumber", roomNumber);
                intent.putExtra("hotelName", hotelName);
            startActivity(intent);
        });

        mStaffCardView.setOnClickListener((View v) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Calling Consierge")
                    .setMessage("Please use this function if you want to get help with your luggage, or if you have any special request. This will call someone to your room.")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("CALL STAFF", (DialogInterface arg0, int arg1) ->
                            callStaff()
                    ).create().show();
        });


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (DialogInterface arg0, int arg1) ->
                    android.os.Process.killProcess(android.os.Process.myPid())
                ).create().show();
    }

    public void callStaff() {
        Map<String, Object> task = new HashMap<>();
        task.put("roomNumber", Integer.parseInt(roomNumber));
        task.put("taskStatus", "todo");
        task.put("taskType", "Assistance needed");
        task.put("date", Timestamp.now());
        db.collection("hotels").document(hotelName).collection("tasks").document()
                .set(task);
    }



}
