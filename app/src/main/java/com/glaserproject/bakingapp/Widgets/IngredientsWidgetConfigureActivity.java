package com.glaserproject.bakingapp.Widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.R;
import com.glaserproject.bakingapp.RvAdapters.RecipesAdapter;

import java.util.List;

public class IngredientsWidgetConfigureActivity extends Activity implements RecipesAdapter.RecipesAdapterOnClickHandler {


    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    RecipesAdapter mAdapter;


    public IngredientsWidgetConfigureActivity() {
        super();
    }

    //save recipeId into SharedPrefs
    static void saveSelectedRecipe(Context context, int appWidgetId, int selectedRecipe, String recipeName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, 0).edit();
        prefs.putInt(AppConstants.PREF_PREFIX_KEY + appWidgetId, selectedRecipe);
        prefs.putString(AppConstants.PREFS_RECIPE_NAME + appWidgetId, recipeName);
        prefs.apply();
    }

    //calc the number of columns to create
    public static int calculateNoColumns(Context context) {
        //get Display Metrics
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        //min width for column
        int scalingFactor = 400;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        //set at least 2 columns
        if (noOfColumns < 1)
            noOfColumns = 1;
        return noOfColumns;
    }

    @Override
    protected void onCreate(@Nullable Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredients_widget_configure);

        //setup RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoColumns(this));
        RecyclerView recipeListRV = findViewById(R.id.widget_config_rv);
        recipeListRV.setLayoutManager(layoutManager);
        mAdapter = new RecipesAdapter(this);
        recipeListRV.setAdapter(mAdapter);

        //get recipes
        retrieveRecipes();


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();

        }

    }

    @Override
    public void onClick(Recipe recipe) {
        final Context context = IngredientsWidgetConfigureActivity.this;

        // When the button is clicked, store the recipe Id
        int selectedRecipe = recipe.id;
        String recipeName = recipe.name;
        saveSelectedRecipe(context, mAppWidgetId, selectedRecipe, recipeName);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        IngredientsWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    //retrieve recipes
    public void retrieveRecipes() {
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                RecipeDatabase mDb = RecipeDatabase.getInstance(getBaseContext());
                List<Recipe> recipes = mDb.recipeDAO().loadRecipesList();
                mAdapter.setRecipes(recipes);
            }
        });
    }
}
