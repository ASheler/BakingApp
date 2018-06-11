package com.glaserproject.bakingapp.NetUtils;

import android.arch.persistence.room.TypeConverter;

import com.glaserproject.bakingapp.Objects.Ingredient;
import com.glaserproject.bakingapp.Objects.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;


public class ListConverter {

    static Gson gson = new Gson();

    @TypeConverter
    public static String ingredientsToString(List<Ingredient> ingredients){
        return gson.toJson(ingredients);
    }

    @TypeConverter
    public static List<Ingredient> stringToIngredients(String data){
        if (data == null){
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {}.getType();

        return gson.fromJson(data, listType);
    }


    @TypeConverter
    public static String stepsToString(List<Step> steps){
        return gson.toJson(steps);
    }

    @TypeConverter
    public static List<Step> stringToSteps(String data){
        if (data == null){
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {}.getType();

        return gson.fromJson(data, listType);
    }


}
