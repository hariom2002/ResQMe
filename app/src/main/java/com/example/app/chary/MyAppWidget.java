package com.example.app.chary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class MyAppWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);

        // set the ImageView
        views.setImageViewResource(R.id.imageView_widget, R.mipmap.app_icon);

        // Handle the widget click event
        Intent i1 = new Intent(context, MainActivity.class);
        PendingIntent p1 = PendingIntent.getActivity(context, appWidgetId, i1, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.my_linear_layout, p1);


        // Handle the button click event
        Intent i2 = new Intent(context, PowerButtonService.class);
        i2.putExtra(MyReceiver.SOS_TRIGGERED, true);
        PendingIntent p2 = PendingIntent.getService(context, appWidgetId, i2, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button_widget, p2);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}