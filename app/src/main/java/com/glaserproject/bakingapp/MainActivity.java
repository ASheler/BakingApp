package com.glaserproject.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.glaserproject.bakingapp.NetUtils.ATLoader;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.ViewModels.MainViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>>{

    RecipeDatabase mDb;
    private static final int FETCH_DATA_LOADER_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = RecipeDatabase.getInstance(this);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Recipe>> loader = loaderManager.getLoader(FETCH_DATA_LOADER_ID);
        if (loader == null){
            loaderManager.initLoader(FETCH_DATA_LOADER_ID, null, this).forceLoad();
        } else {
            loaderManager.restartLoader(FETCH_DATA_LOADER_ID, null, this);
        }

        retrieveRecipes();



    }

    public void retrieveRecipes(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        List<Recipe> recipes = viewModel.getRecipes();
    }


    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new ATLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, final List<Recipe> recipes) {
        //insert recipes into Db
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.recipeDAO().insertRecipes(recipes);
                retrieveRecipes();
            }
        });
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {

    }
}