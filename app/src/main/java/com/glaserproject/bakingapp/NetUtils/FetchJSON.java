package com.glaserproject.bakingapp.NetUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.glaserproject.bakingapp.MainActivity;
import com.glaserproject.bakingapp.Objects.Recipe;

import java.net.URL;
import java.util.List;

public class FetchJSON extends AsyncTask<String, Void, List<Recipe>> {

    URL url;

    RecipeDatabase mDb;

    public FetchJSON (URL url, RecipeDatabase database){
        this.url = url;
        this.mDb = database;
    }


    @Override
    protected List<Recipe> doInBackground(String... strings) {
        try {
            //get JSON from URL
            String jsonResponse = JSONutils.getJSONFromUrl(url);

            //parse data from JSON
            List<Recipe> recipes = JSONutils.parseJSON(jsonResponse);
            return recipes;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(final List<Recipe> recipes) {
        super.onPostExecute(recipes);


        //insert recipes into Db
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.recipeDAO().insertRecipes(recipes);
            }
        });

    }
}
