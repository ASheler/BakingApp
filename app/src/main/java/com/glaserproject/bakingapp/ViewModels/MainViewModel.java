package com.glaserproject.bakingapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Recipe>> recipes;

    public MainViewModel(@NonNull Application application) {
        super(application);

        RecipeDatabase mDb = RecipeDatabase.getInstance(this.getApplication());
        recipes = mDb.recipeDAO().loadAllRecipes();

    }

    public LiveData<List<Recipe>> getRecipes(){
        return recipes;
    }


}
