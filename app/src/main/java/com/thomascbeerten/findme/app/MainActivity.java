package com.thomascbeerten.findme.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    TextView txtPhoneName;
    TextView txtPhoneNumber;
    Button btnFindContact;
    Button btnSendLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //views initializeren
        btnFindContact = (Button) findViewById(R.id.btnFindContact);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        txtPhoneName = (TextView) findViewById(R.id.txtPhoneName);
        btnSendLocation = (Button) findViewById(R.id.btnSendLocation);

        //buttoncontact
        btnFindContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

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
    }

    private void ShowContact(String name, String number) {
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
    }
}