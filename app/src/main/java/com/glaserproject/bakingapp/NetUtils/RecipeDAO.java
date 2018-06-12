package com.glaserproject.bakingapp.NetUtils;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Query("SELECT * FROM recipes ORDER BY id")
    LiveData<List<Recipe>> loadAllRecipes();

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateRecipe(List<Recipe> recipe);

    @Insert
    void insertRecipe(Recipe recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipes(List<Recipe> recipes);

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe loadRecipe(int id);

    @Query("SELECT * FROM recipes WHERE id = :id")
    List<Step> loadSteps(int id);

}
