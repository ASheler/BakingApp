package com.glaserproject.bakingapp.NetUtils;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.glaserproject.bakingapp.Objects.Recipe;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Query("SELECT * FROM recipes ORDER BY id")
    List<Recipe> loadAllRecipes();

    @Insert
    void insertRecipe(Recipe recipe);

    @Insert
    void insertRecipes(List<Recipe> recipes);

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe loadRecipe(int id);

}
