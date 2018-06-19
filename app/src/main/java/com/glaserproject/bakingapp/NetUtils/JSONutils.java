package com.glaserproject.bakingapp.NetUtils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.glaserproject.bakingapp.Objects.Ingredient;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JSONutils {

    //URL variables
    private final static String RECIPE_JSON_BASE = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking";
    private final static String RECIPE_JSON_APPEND = "baking.json";



    //get stream from URL
    public static String getJSONFromUrl(URL url) throws IOException {

        //open url connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            //setup scanner
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                //return scanned item
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            //close URL connection
            urlConnection.disconnect();
        }

    }


    public static URL buildUrl() {
        //build URL
        Uri builtUri = Uri.parse(RECIPE_JSON_BASE).buildUpon()
                .appendPath(RECIPE_JSON_APPEND)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    //Parse JSON to List<Recipe>
    public static List<Recipe> parseJSON(String json) throws JSONException {

        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<Recipe>>(){}.getType();

        List<Recipe> recipe = gson.fromJson(json, listType);

        return recipe;
    }


    public static String getIng(Context context) {

        RecipeDatabase mDb = RecipeDatabase.getInstance(context);
        Recipe recipe = mDb.recipeDAO().loadRecipe(1);
        String ingredients;

        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingredient : recipe.ingredients) {
            stringBuilder.append(ingredient.getIngredient());
            stringBuilder.append(" - ");
            stringBuilder.append(ingredient.getQuantity());
            stringBuilder.append(" ");
            stringBuilder.append(ingredient.getMeasure());
            stringBuilder.append("\n \n");
        }

        ingredients = stringBuilder.toString();

        return ingredients;

    }


}
