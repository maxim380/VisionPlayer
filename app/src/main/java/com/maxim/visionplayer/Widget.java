package com.maxim.visionplayer;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    private static final String PlayOnClick = "PlayClickTag";
    private static final String PrevOnClick = "PrevClickTag";
    private static final String NextOnClick = "NextClickTag";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, Widget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
            thisWidget = new ComponentName(context, Widget.class);
            remoteViews.setOnClickPendingIntent(R.id.widget_next_button, getPendingSelfIntent(context, NextOnClick));
            remoteViews.setOnClickPendingIntent(R.id.widget_prev_button, getPendingSelfIntent(context, PrevOnClick));
            remoteViews.setOnClickPendingIntent(R.id.widget_play_button, getPendingSelfIntent(context, PlayOnClick));
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void test() {
        System.out.print("Hoi");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line
        if (PlayOnClick.equals(intent.getAction())) {
            startApp(context);
        } else if (PrevOnClick.equals(intent.getAction())) {
            startApp(context);
        } else if (NextOnClick.equals(intent.getAction())) {
            startApp(context);
        }
    }

    private void startApp(Context context) {

    }
}

