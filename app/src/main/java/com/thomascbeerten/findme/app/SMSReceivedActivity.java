package com.thomascbeerten.findme.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SMSReceivedActivity extends Activity {
    //variabelen
    String SMS;
    Button btnShowReceivedLocation;
    GoogleMap map;
    LatLng LOCATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsreceived);


        //get latitude & longitude from sms
        SMS = getIntent().getStringExtra("SMS");
        String latitude = SMS.split("\\*")[1];
        String longitude = SMS.split("\\*")[2];

        LOCATION = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));

        btnShowReceivedLocation = (Button) findViewById(R.id.btnShowReceivedLocation);
        btnShowReceivedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateMap();
            }
        });
    }

    private void UpdateMap() {
        if (LOCATION != null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.addMarker(new MarkerOptions().position(LOCATION).title("reveived location"));
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION, 16);
            map.animateCamera(update);
        }
    }
}
