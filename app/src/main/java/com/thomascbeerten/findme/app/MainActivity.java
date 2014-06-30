package com.thomascbeerten.findme.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity {
    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    TextView txtPhoneName;
    TextView txtPhoneNumber;
    Button btnFindContact;
    Button btnSendLocation;
    TextView txtSms;
    Button btnLocationStuff;

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    IntentFilter intentFilter;

    //map
    private GoogleMap map;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("called", "intentreceiver called");

            txtSms.setVisibility(View.VISIBLE);
            txtSms.setText(intent.getExtras().getString("sms"));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //---unregister the receiver---
        unregisterReceiver(intentReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //views initializeren
        btnFindContact = (Button) findViewById(R.id.btnFindContact);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        txtPhoneName = (TextView) findViewById(R.id.txtPhoneName);
        btnSendLocation = (Button) findViewById(R.id.btnSendLocation);
        txtSms = (TextView) findViewById(R.id.txtSMS);
        btnLocationStuff = (Button) findViewById(R.id.btnLocationStuff);

        //buttoncontact
        btnFindContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

        //buttonlocationstuff
        btnLocationStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationStuff();
            }
        });

        //---intent to filter for SMS messages received---
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projectionnumber = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                String[] projectionname = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursornumber = getContentResolver()
                        .query(contactUri, projectionnumber, null, null, null);
                cursornumber.moveToFirst();

                Cursor cursorname = getContentResolver()
                        .query(contactUri, projectionname, null, null, null);
                cursorname.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int columnnumber = cursornumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursornumber.getString(columnnumber);

                // Retrieve the phone name from the DISPLAY_NAME column
                int columnname = cursorname.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursorname.getString(columnname);


                // Do something with the phone number...
                ShowContact(name, number);
            } else {
                HideContact();
            }
        }
    }

    private void HideContact() {
        txtPhoneNumber.setVisibility(View.GONE);
        txtPhoneName.setVisibility(View.GONE);
        btnSendLocation.setEnabled(false);
        txtSms.setVisibility(View.GONE);
    }

    private void ShowContact(String name, final String number) {
        txtPhoneNumber.setVisibility(View.VISIBLE);
        txtPhoneName.setVisibility(View.VISIBLE);
        if (name != null) {
            txtPhoneName.setText(name);
        }
        if (number != null) {
            txtPhoneNumber.setText(number);
        }
        if (name != null && number != null) {
            if (name.equals(number)) {
                txtPhoneName.setText("Unknown");
            }
        }
        btnSendLocation.setEnabled(true);
        btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  sendSms(number, "testsms for debugging");
                sendSms("0473848248", "testsms for debugging");
            }
        });
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void LocationStuff() {
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                TextView textViewLocationStuff = (TextView) findViewById(R.id.textViewLocationStuff);
                textViewLocationStuff.setText(location.toString());

                final LatLng LOCATION = new LatLng(location.getLatitude(), location.getLongitude());
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                map.addMarker(new MarkerOptions().position(LOCATION).title("find me here"));
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION, 16);
                map.animateCamera(update);
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }
}