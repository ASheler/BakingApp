package com.glaserproject.bakingapp.NetUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.glaserproject.bakingapp.Objects.Recipe;

import java.util.List;

public class ATLoader extends AsyncTaskLoader<List<Recipe>> {

    public ATLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }


    @Nullable
    @Override
    public List<Recipe> loadInBackground() {
        try {
            //get JSON from URL
            String jsonResponse = JSONutils.getJSONFromUrl(JSONutils.buildUrl());

            //parse data from JSON
            List<Recipe> recipes = JSONutils.parseJSON(jsonResponse);
            return recipes;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
