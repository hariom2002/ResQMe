package com.example.app.chary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static int mCount = 0;
    private static long mTimeStamp = 0;         // timestamp in millisecond

    public static final String SOS_TRIGGERED = "sos_triggered";
    public static final String BUTTON_PRESSED = "button_pressed";

    public static final String ACTION_SMS_SENT = "mca.project.sos.app.SMS_SENT";
    public static final String ACTION_SMS_DELIVERED = "mca.project.sos.app.SMS_DELIVERED";



    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case ACTION_SMS_SENT:
                //Toast.makeText(context, "SOS Message Sent", Toast.LENGTH_LONG).show();

                Log.d(MainActivity.TAG, "SOS Message Sent");

                break;

            case ACTION_SMS_DELIVERED:
                //Toast.makeText(context, "SOS Message Delivered", Toast.LENGTH_LONG).show();

                Log.d(MainActivity.TAG, "SOS Message Delivered");

                break;

            default:
                doPowerButtonCount(context);
        }

    }



    private void doPowerButtonCount(Context context) {
        long ts = System.currentTimeMillis() / 1000;

        //Log.d(MainActivity.TAG, "mTimeStamp = " + mTimeStamp);
        //Log.d(MainActivity.TAG, "TS = " + ts);


        if (mCount == 0 && mTimeStamp == 0) {
            initCounter(ts);
        }
        else {
            // compare the timestamps
            if ( (ts - mTimeStamp) <= 3 ) {
                mTimeStamp = ts;

                Log.d(MainActivity.TAG, "Going towards...");

                mCount++;
            }
            else {
                // Cancel the operation
                Log.d(MainActivity.TAG, "ABORTED!!!");

                // start counting again
                initCounter(ts);
            }
        }

        // Did we get 4 continuous Power Button Presses
        if (mCount == 4) {
            Intent i = new Intent(context, PowerButtonService.class);
            i.putExtra(SOS_TRIGGERED, true);
            i.putExtra(BUTTON_PRESSED, mCount);

            resetCounter();

            // start the SOS service
            context.startService(i);
        }
    }



    private void initCounter(long ts) {
        mTimeStamp = ts;
        mCount = 1;

        Log.d(MainActivity.TAG, "Counting starts...");
    }


    private void resetCounter() {
        // reset the counter
        MyReceiver.mCount = 0;
        MyReceiver.mTimeStamp = 0;
    }
}