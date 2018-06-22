package com.glaserproject.bakingapp.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.MainActivity;
import com.glaserproject.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {


    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_layout);

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppConstants.APP_WIDGET_ID_KEY, appWidgetId);
        views.setRemoteAdapter(R.id.widget_list_view, intent);


        //TODO: recipes are not changing in different widgets

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            //updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_layout);

            Intent intent = new Intent(context, WidgetRemoteViewsService.class);
            intent.putExtra(AppConstants.APP_WIDGET_ID_KEY, appWidgetId);
            views.setRemoteAdapter(R.id.widget_list_view, intent);


            //TODO: recipes are not changing in different widgets

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

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

