package com.rs.roomservice.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.rs.roomservice.R;

public class HotelInfoActivity extends AppCompatActivity implements OnMapReadyCallback {


    private Button websiteButton;
    private Button centerMapButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String hotelName;
    private TextView hotelNameTextView;
    private TextView hotelAddressTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_info);

        Intent getHotelIntent = getIntent();
        Bundle extras = getHotelIntent.getExtras();

        if(extras != null) {
           hotelName = extras.getString("hotelName");
        }

        hotelNameTextView = findViewById(R.id.tv_hotel_name);
        hotelAddressTextView = findViewById(R.id.tv_hotel_address);

        setHotelData();


        websiteButton = findViewById(R.id.btn_open_website);
        centerMapButton = findViewById(R.id.btn_refresh_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        centerMapButton.setOnClickListener((View v) ->
                mapFragment.getMapAsync(this)
        );

        websiteButton.setOnClickListener((View v) ->
            openWebPage("http://www.google.com/search?q=hotel+krakow")
        );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        DocumentReference hotelRef = db.collection("hotels").document(hotelName);
        hotelRef.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        GeoPoint geoPoint = snapshot.getGeoPoint("geolocation");
                        double lat = geoPoint.getLatitude();
                        double lng = geoPoint.getLongitude();
                        LatLng hotelAddress = new LatLng(lat, lng);
                        googleMap.addMarker(new MarkerOptions().position(hotelAddress)
                                .title("Hotel Location Marker"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(hotelAddress));
                    } else {
                        LatLng hotelAddress = new LatLng(50.049683, 19.944544);
                        googleMap.addMarker(new MarkerOptions().position(hotelAddress)
                                .title("Krakow"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(hotelAddress));
                    }
                });

    }

    public void openWebPage(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void setHotelData() {
        DocumentReference hotelRef = db.collection("hotels").document(hotelName);
        hotelRef.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
            DocumentSnapshot snapshot = task.getResult();
            if (snapshot.exists()) {
                    String hotelAddress = snapshot.getString("address");
                    hotelNameTextView.setText(hotelName);
                    hotelAddressTextView.setText(hotelAddress);
            }
        });
    }


}
