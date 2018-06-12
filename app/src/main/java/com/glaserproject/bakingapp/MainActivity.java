package com.glaserproject.bakingapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.glaserproject.bakingapp.NetUtils.ATLoader;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.RvAdapters.RecipesAdapter;
import com.glaserproject.bakingapp.ViewModels.MainViewModel;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>>{

    RecipeDatabase mDb;
    private static final int FETCH_DATA_LOADER_ID = 123;
    ViewModel mainViewModel;
    RecipesAdapter mAdapter;

    @BindView(R.id.recipe_list_rv)
    RecyclerView recipeListRV;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init ButterKnife
        butterknife.ButterKnife.bind(this);

        //init rv
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recipeListRV.setLayoutManager(layoutManager);
        mAdapter = new RecipesAdapter();
        recipeListRV.setAdapter(mAdapter);

        //init viewModel
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);






        mDb = RecipeDatabase.getInstance(this);

        if (isNetworkAvailable(this)) {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<Recipe>> loader = loaderManager.getLoader(FETCH_DATA_LOADER_ID);
            if (loader == null){
                loaderManager.initLoader(FETCH_DATA_LOADER_ID, null, this).forceLoad();
            } else {
                loaderManager.restartLoader(FETCH_DATA_LOADER_ID, null, this);
            }
        } else {
            Toast.makeText(this, "Connection not available", Toast.LENGTH_SHORT).show();
        }



    }

    public void retrieveRecipes(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mAdapter.setRecipes(recipes);
            }
        });
    }


    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable final Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new ATLoader(this);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, final List<Recipe> recipes) {
        //insert recipes into Db
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                List<Recipe> temp = mDb.recipeDAO().fetchRecipes();
                if (temp.isEmpty()) {
                    mDb.recipeDAO().insertRecipes(recipes);
                }
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
}