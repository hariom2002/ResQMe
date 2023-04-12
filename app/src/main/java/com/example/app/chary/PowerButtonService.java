package com.example.app.chary;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class PowerButtonService extends Service{
    private MyReceiver myReceiver;

    private static final String PREF_FILE_NAME = "com.example.app.contactpickerapp.my_pref";
    private static final String KEY_CONTACTS_NO = "no_of_contacts";

    MyUtilityClass utility;

    SharedPreferences sh;

    public PowerButtonService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(MainActivity.TAG, "PowerButtonService created");

        sh = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);


        // For "ACTION_SCREEN_ON", "ACTION_SCREEN_OFF", "ACTION_USER_PRESENT"
        // Register the broadcast receiver dynamically
        // called when the service gets created                 // only once

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(MyReceiver.ACTION_SMS_SENT);
        filter.addAction(MyReceiver.ACTION_SMS_DELIVERED);

        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, filter);


        utility = new MyUtilityClass(getApplicationContext());
    }



    // do the SOS related work here
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getBooleanExtra(MyReceiver.SOS_TRIGGERED, false)) {

            // Is LOCATION_PERMISSION enable ?
            // Is SMS_PERMISSION enable ?

            // IF ALL ARE YES then start SOS process


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "Turn on the permission(s) first", Toast.LENGTH_LONG).show();
            }
            else {
                Log.d(MainActivity.TAG, "Starting SOS Service...");

                if (utility != null) {

                    utility.startSOSProcess();
                }


                // give user a haptic feedback only when Power button was pressed
                if (intent.getIntExtra(MyReceiver.BUTTON_PRESSED, 0) == 4
                        && sh.getInt(KEY_CONTACTS_NO, 0) >= 2) {

                    Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(1000);
                    }
                }
            }


        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }

        // start the service again
        Log.d(MainActivity.TAG, PowerButtonService.class.getSimpleName() + " destroyed");
    }





    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}
