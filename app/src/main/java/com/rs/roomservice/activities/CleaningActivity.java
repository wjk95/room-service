package com.rs.roomservice.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rs.roomservice.R;

import java.util.HashMap;
import java.util.Map;

public class CleaningActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String roomNumber;
    private String hotelName;

    private CardView mTowelCardView;
    private CardView mRubbishCardView;
    private CardView mBathroomCardView;
    private CardView mCleaningCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaning);

        Intent getRoomNumberIntent = getIntent();
        Bundle extras = getRoomNumberIntent.getExtras();
        if(extras != null) {
            roomNumber = extras.getString("roomNumber");
            hotelName = extras.getString("hotelName");
        }

        mCleaningCardView = findViewById(R.id.card_room_cleaning);
        mTowelCardView = findViewById(R.id.card_towel);
        mRubbishCardView = findViewById(R.id.card_trash);
        mBathroomCardView = findViewById(R.id.card_bathroom);

        mTowelCardView.setOnClickListener((View v) ->
            new AlertDialog.Builder(this)
                    .setTitle("New Towel")
                    .setMessage("Do you want to order a new towel?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("YES", (DialogInterface arg0, int arg1) -> {
                            Map<String, Object> task = new HashMap<>();
                            task.put("roomNumber", Integer.parseInt(roomNumber));
                            task.put("taskStatus", "todo");
                            task.put("taskType", "New Towel");
                            task.put("date", Timestamp.now());
                            db.collection("hotels").document(hotelName).collection("tasks").document()
                            .set(task);
                    }).create().show()
        );

        mRubbishCardView.setOnClickListener((View v) ->
                new AlertDialog.Builder(this)
                        .setTitle("Rubbish Takeout")
                        .setMessage("Do you want someone to take out the rubbish?")
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("YES", (DialogInterface arg0, int arg1) -> {
                            Map<String, Object> task = new HashMap<>();
                            task.put("roomNumber", Integer.parseInt(roomNumber));
                            task.put("taskStatus", "todo");
                            task.put("taskType", "Rubbish Takeout");
                            task.put("date", Timestamp.now());
                            db.collection("hotels").document(hotelName).collection("tasks").document()
                                    .set(task);
                        }).create().show()
        );

        mBathroomCardView.setOnClickListener((View v) ->
                new AlertDialog.Builder(this)
                        .setTitle("Bathroom Replenishment")
                        .setMessage("Do you want someone to clean and replenish your bathroom?")
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("YES", (DialogInterface arg0, int arg1) -> {
                            Map<String, Object> task = new HashMap<>();
                            task.put("roomNumber", Integer.parseInt(roomNumber));
                            task.put("taskStatus", "todo");
                            task.put("taskType", "Bathroom Replenishment");
                            task.put("date", Timestamp.now());
                            db.collection("hotels").document(hotelName).collection("tasks").document()
                                    .set(task);
                        }).create().show()
        );

        mCleaningCardView.setOnClickListener((View v) ->
                new AlertDialog.Builder(this)
                        .setTitle("Room Cleaning")
                        .setMessage("Do you want someone to clean your room?")
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("YES", (DialogInterface arg0, int arg1) -> {
                            Map<String, Object> task = new HashMap<>();
                            task.put("roomNumber", Integer.parseInt(roomNumber));
                            task.put("taskStatus", "todo");
                            task.put("taskType", "Room Cleaning");
                            task.put("date", Timestamp.now());
                            db.collection("hotels").document(hotelName).collection("tasks").document()
                                    .set(task);
                        }).create().show()
        );

    }
}
