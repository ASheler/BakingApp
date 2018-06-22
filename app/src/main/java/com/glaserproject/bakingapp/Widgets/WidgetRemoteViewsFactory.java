package com.glaserproject.bakingapp.Widgets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.R;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Recipe recipe;
    private int recipeId;
    private int appWidgetId;


    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        appWidgetId = intent.getIntExtra(AppConstants.APP_WIDGET_ID_KEY, 1);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        final long identityToken = Binder.clearCallingIdentity();

        RecipeDatabase mDb = RecipeDatabase.getInstance(mContext);
        SharedPreferences prefs = mContext.getSharedPreferences(AppConstants.PREFS_NAME, 0);
        recipeId = prefs.getInt(AppConstants.PREF_PREFIX_KEY + appWidgetId, 9999);


        recipe = mDb.recipeDAO().loadRecipe(recipeId);


        Binder.restoreCallingIdentity(identityToken);


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (recipe == null) {
            return 0;
        } else {
            return recipe.ingredients.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position > recipe.ingredients.size()) {
            return null;
        }


        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(recipe.ingredients.get(position).getIngredient());
        stringBuilder.append(" (");
        stringBuilder.append(recipe.ingredients.get(position).getQuantity());
        stringBuilder.append("\u00A0");
        stringBuilder.append(recipe.ingredients.get(position).getMeasure());
        stringBuilder.append(")");

        String ingredientsText = stringBuilder.toString();


        remoteViews.setTextViewText(R.id.widget_text, ingredientsText);

        //set Click intent for items
        Intent fillInIntent = new Intent();
        //put recipe
        fillInIntent.putExtra(AppConstants.RECIPE_EXTRA_KEY, recipe);
        remoteViews.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
