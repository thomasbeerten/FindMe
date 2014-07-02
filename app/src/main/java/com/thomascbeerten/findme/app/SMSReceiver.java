package com.thomascbeerten.findme.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zi04 on 30/06/2014.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "FINDME SMS from ";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (i == 0) {
                    //---get the sender address/phone number---
                    str += msgs[i].getOriginatingAddress();
                    str += ": ";
                }
                //---get the message body---
                str += msgs[i].getMessageBody().toString();
            }
            //---display the new SMS message---
            // Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            Log.d("SMSReceiver", str);


            Log.d("SMS receiver class: substring", str.substring(0, 6));

            //indien inkomende sms > 6 tekens en begint met FINDME, doorsturen naar mainactivity
            if (str.length() > 6) {
                if (str.substring(0, 6).equals("FINDME")) {
                    //SMS intern afhandelen en niet verder broadcasten
                    this.abortBroadcast();
                    //intent naar mainactivity
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("SMS_RECEIVED_ACTION");
                    broadcastIntent.putExtra("sms", str);
                    context.sendBroadcast(broadcastIntent);
                }
            }

        }
    }
}
