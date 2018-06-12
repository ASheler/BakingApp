package com.glaserproject.bakingapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;

import java.util.List;

public class StepsViewModel extends AndroidViewModel {

    private List<Step> steps;

    public StepsViewModel(@NonNull Application application, int id) {
        super(application);

        RecipeDatabase mDb = RecipeDatabase.getInstance(this.getApplication());
        steps = mDb.recipeDAO().loadSteps(id);
    }

    public List<Step> getSteps() {
        return steps;
    }


}
