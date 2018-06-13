package com.glaserproject.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.NetUtils.ATLoader;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.RvAdapters.RecipesAdapter;
import com.glaserproject.bakingapp.ViewModels.MainViewModel;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Recipe>>,
        RecipesAdapter.RecipesAdapterOnClickHandler {


    RecipeDatabase mDb;
    ViewModel mainViewModel;
    RecipesAdapter mAdapter;

    //loader ID
    private static final int FETCH_DATA_LOADER_ID = 123;


    //bind views via ButterKnife
    @BindView(R.id.recipe_list_rv)
    RecyclerView recipeListRV;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init ButterKnife
        butterknife.ButterKnife.bind(this);

        //init rv
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoColumns(this));
        recipeListRV.setLayoutManager(layoutManager);
        mAdapter = new RecipesAdapter(this);
        recipeListRV.setAdapter(mAdapter);

        //init viewModel
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        //init Database
        mDb = RecipeDatabase.getInstance(this);

        //Check if network is available
        if (isNetworkAvailable(this)) {
            //if available, run loader
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<Recipe>> loader = loaderManager.getLoader(FETCH_DATA_LOADER_ID);
            //check if loader is running
            if (loader == null){
                //run new loader
                loaderManager.initLoader(FETCH_DATA_LOADER_ID, null, this).forceLoad();
            } else {
                //restart loader
                loaderManager.restartLoader(FETCH_DATA_LOADER_ID, null, this).forceLoad();
            }

            //if no connection, print toast
        } else {

            Toast.makeText(this, "Connection not available", Toast.LENGTH_SHORT).show();
        }



    }


    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable final Bundle args) {
        //set progress bar and run Loader
        progressBar.setVisibility(View.VISIBLE);
        return new ATLoader(this);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, final List<Recipe> recipes) {
        //insert recipes into Db
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                //hide progress bar
                progressBar.setVisibility(View.INVISIBLE);
                //insert recipes into DB
                mDb.recipeDAO().insertRecipes(recipes);
                //load recipes
                retrieveRecipes();
            }
        });
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {
    }



    //Check if Network connection is available
    public boolean isNetworkAvailable(Context context) {
        //set connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //check if ActiveNetwork isn't null && is Connected
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    //get recipes from ViewModel method
    public void retrieveRecipes() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable final List<Recipe> recipes) {
                //set recipes to adapter
                //run on UI thread so we can notify and change data
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setRecipes(recipes);
                    }
                });

            }
        });
    }

    //onClick method for RV
    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeViewActivity.class);
        intent.putExtra(AppConstants.RECIPE_EXTRA_KEY, recipe);
        startActivity(intent);
    }


}