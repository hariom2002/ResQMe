package com.example.app.chary;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


public class MyUtilityClass {

    private static final String KEY_CON1 = "con1";
    private static final String KEY_CON2 = "con2";
    private static final String KEY_CON3 = "con3";
    private static final String KEY_CONTACTS_NO = "no_of_contacts";

    private static final String DELIMITER = "/";


    FusedLocationProviderClient mLocationProviderClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    Context mContext;

    private int mCounter = 0;

    private static final String PREF_FILE_NAME = "com.example.app.contactpickerapp.my_pref";

    private String[] phoneNumbers;
    private SharedPreferences sh;


    public MyUtilityClass(final Context mContext) {
        this.mContext = mContext;

        sh = mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);


        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                mCounter++;

                Log.d(MainActivity.TAG, location.toString());

                if (mCounter == 3) {

                    // get at max three location updates
                    // and then send sms

                    String url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f",
                            location.getLatitude(), location.getLongitude());

                    sendSMS(url, phoneNumbers);
                }
            }
        };

    }



    public void startSOSProcess() {

        int emgContacts = sh.getInt(KEY_CONTACTS_NO, 0);

        Log.d(MainActivity.TAG, emgContacts + "");


        if (emgContacts == 0) {
            Toast.makeText(mContext, "Add emergency contacts first", Toast.LENGTH_LONG).show();
        }
        else if (emgContacts >= 2) {
            initProcess();
        }
        else {
            Toast.makeText(mContext, "Add one more emergency contact to send SOS", Toast.LENGTH_LONG).show();
        }
    }



    @SuppressLint("MissingPermission")
    private void initProcess() {

        phoneNumbers = new String[]{
                sh.getString(KEY_CON1, ""),
                sh.getString(KEY_CON2, ""),
                sh.getString(KEY_CON3, "")
        };


        for (int i = 0; i < phoneNumbers.length; i++) {
            if (!phoneNumbers[i].isEmpty()) {

                phoneNumbers[i] = phoneNumbers[i].split(DELIMITER)[1];
            }
        }


        Log.d(MainActivity.TAG, Arrays.toString(phoneNumbers));



        if (mLocationProviderClient != null) {
            mCounter = 0;

            mLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper() /* null */);
        }
    }


    private void sendSMS(String mapUrl,  String[] phNo) {

        // remove periodic location updates
        mLocationProviderClient.removeLocationUpdates(mLocationCallback);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(mContext);

//        String sosMsg = sh.getString("user_msg",mContext.getString(R.string.default_msg));
//
//        SmsManager smsManager = SmsManager.getDefault();
//        String smsBody = sosMsg + "\n" + mapUrl;

        //////// added //////////
        String smsBody = sh.getString("user_msg",mContext.getString(R.string.default_msg));

        SmsManager smsManager = SmsManager.getDefault();

        if(sh.getBoolean("send_time_switch",true)){
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            smsBody += "\n"+date;
        }

        if(sh.getBoolean("send_location_switch",true))
            smsBody += "\n" + mapUrl;

        //////////////////


        PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 20, new Intent(MyReceiver.ACTION_SMS_SENT),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deliveryIntent = PendingIntent.getBroadcast(mContext, 73, new Intent(MyReceiver.ACTION_SMS_DELIVERED),
                PendingIntent.FLAG_UPDATE_CURRENT);


        for (String p : phNo) {
            if (!p.isEmpty()) {
                smsManager.sendTextMessage(p, null, smsBody, sentIntent, deliveryIntent);
            }
        }

    }
}