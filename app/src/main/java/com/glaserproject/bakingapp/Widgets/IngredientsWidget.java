package com.glaserproject.bakingapp.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.MainActivity;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.R;
import com.glaserproject.bakingapp.RecipeViewActivity;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {


    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_layout);

        //set Recipe Name
        String name = getRecipeName(context, appWidgetId);

        views.setTextViewText(R.id.widget_recipe_label, name);


        //send intent and data to Adapter
        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppConstants.APP_WIDGET_ID_KEY, appWidgetId);

        //set random data to initialize new Factory
        Random rnd = new Random();
        intent.setData(Uri.fromParts("content", String.valueOf(rnd.nextInt()), null));

        views.setRemoteAdapter(R.id.widget_list_view, intent);

        // template to handle the click listener listView
        Intent clickIntentTemplate = new Intent(context, RecipeViewActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemplate);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    //get SharedPrefs and get Recipe name from Db
    private static String getRecipeName(final Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, 0);
        String recipeName = prefs.getString(AppConstants.PREFS_RECIPE_NAME + appWidgetId, "N/A");

        return recipeName;
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

