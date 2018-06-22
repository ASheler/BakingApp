package com.glaserproject.bakingapp.Widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Ingredient;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.R;

import java.lang.ref.WeakReference;

public class GetIngredientsAsync extends AsyncTask<Void, Void, String> {

    private final WeakReference<Context> weakContextReference;
    private RemoteViews views;
    private int widgetID;
    private AppWidgetManager widgetManager;

    public GetIngredientsAsync(RemoteViews remoteViews, int appWidgetId, AppWidgetManager appWidgetManager, Context context) {
        this.views = remoteViews;
        this.widgetID = appWidgetId;
        this.widgetManager = appWidgetManager;
        this.weakContextReference = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(Void... voids) {
        RecipeDatabase mDb = RecipeDatabase.getInstance(weakContextReference.get());
        Recipe recipe = mDb.recipeDAO().loadRecipe(1);
        String ingredients;

        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingredient : recipe.ingredients) {
            stringBuilder.append(ingredient.getIngredient());
            stringBuilder.append(" - ");
            stringBuilder.append(ingredient.getQuantity());
            stringBuilder.append(" ");
            stringBuilder.append(ingredient.getMeasure());
            stringBuilder.append("\n");
        }

        ingredients = stringBuilder.toString();
        return ingredients;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //views.setTextViewText(R.id.ingredients_widget_tv, s);
        widgetManager.updateAppWidget(widgetID, views);
    }
}
