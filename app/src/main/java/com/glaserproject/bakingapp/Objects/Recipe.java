package com.glaserproject.bakingapp.Objects;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.glaserproject.bakingapp.NetUtils.ListConverter;

import java.util.List;

//set up table via ROOM
@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = false)
    public int id;
    public String name;
    @TypeConverters(ListConverter.class)
    public List<Ingredient> ingredients;
    @TypeConverters(ListConverter.class)
    public List<Step> steps;
    public int servings;
    public String image;


    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }
}
