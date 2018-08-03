package com.example.maola.dtourmap.reportActivities;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.maola.dtourmap.BuildConfig;
import com.example.maola.dtourmap.R;
import com.example.maola.dtourmap.Utility.Constants;
import com.example.maola.dtourmap.Utility.StringUtils;

/**
 * Implementation of App Widget functionality.
 */
public class DTourWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.custom_info_contents);

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        String address= sharedPreferences.
                getString(Constants.wAddress,context.getString(R.string.no_data_widget));
        String category = sharedPreferences.getString(Constants.wCategory, "");
        String user = sharedPreferences.getString(Constants.wUser, "No name");
        long postingTime = sharedPreferences.getLong(Constants.wTime, 0);
        long reportTime = sharedPreferences.getLong(Constants.wReportTime, 0);

        if(postingTime != 0 || reportTime != 0) {
            String sPostingTime = StringUtils.getDate(postingTime, true);
            String sReportTime = StringUtils.getDate(reportTime, false);

            views.setTextViewText(R.id.info_posted_by, user + ", " + sPostingTime);
            views.setTextViewText(R.id.info_address, address);
            views.setTextViewText(R.id.info_date,category + ", " + sReportTime);
        } else {
            views.setTextViewText(R.id.info_address, address);
        }


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

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

