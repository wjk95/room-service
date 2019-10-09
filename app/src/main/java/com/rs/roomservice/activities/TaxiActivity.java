package com.rs.roomservice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rs.roomservice.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TaxiActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private int day, month, year, hour, minute;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private String dateOrdered;
    private TextView mTaxiOrderTextView;
    private Button taxiCancel;
    private Button taxiSaveOrder;
    private String roomNumber;
    private String hotelName;
    private Boolean isDateSet = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);

        mTaxiOrderTextView = findViewById(R.id.tv_taxi_picker);
        mTaxiOrderTextView.setPaintFlags(mTaxiOrderTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        isDateSet = false;
        mTaxiOrderTextView.setText("Click to set the time");

        taxiCancel = findViewById(R.id.btn_cancel_order);
        taxiSaveOrder = findViewById(R.id.btn_taxi_order);


        Intent getRoomNumberIntent = getIntent();
        Bundle extras = getRoomNumberIntent.getExtras();
        if(extras != null) {
            roomNumber = extras.getString("roomNumber");
            hotelName = extras.getString("hotelName");
        }

        mTaxiOrderTextView.setOnClickListener((View pickerView) -> {

            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(TaxiActivity.this, TaxiActivity.this,
                    year, month, day);
            datePickerDialog.show();

        });


        taxiCancel.setOnClickListener((View v1) -> {
            Intent intent = new Intent(TaxiActivity.this, ClientActivity.class);
            startActivity(intent);
            finish();

        });

        taxiSaveOrder.setOnClickListener((View v1) -> {

            if(isDateSet) {

                Toast.makeText(getApplicationContext(), "Taxi will be ordered", Toast.LENGTH_LONG).show();
                Map<String, Object> task = new HashMap<>();
                task.put("roomNumber", Integer.parseInt(roomNumber));
                task.put("taskStatus", "todo");
                task.put("taskType", "Taxi at " + dateOrdered);
                task.put("date", Timestamp.now());
                db.collection("hotels").document(hotelName).collection("tasks").document()
                        .set(task);
               finish();

            }
        });


    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(TaxiActivity.this, TaxiActivity.this,
                hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        hourFinal = i;
        minuteFinal = i1;
        dateOrdered = String.format("%02d:%02d, %02d/%02d", hourFinal, minuteFinal, dayFinal, monthFinal);
        mTaxiOrderTextView.setText(dateOrdered);
        isDateSet = true;

    }



}
