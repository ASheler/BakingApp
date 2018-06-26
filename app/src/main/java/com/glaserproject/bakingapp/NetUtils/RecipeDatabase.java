package com.glaserproject.bakingapp.NetUtils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.glaserproject.bakingapp.Objects.Recipe;

@Database(entities = {Recipe.class}, version = 3, exportSchema = false)
@TypeConverters(ListConverter.class)
public abstract class RecipeDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DB_NAME = "recipes";
    private static RecipeDatabase sInstance;

    public static RecipeDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                //build DB
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class,
                        DB_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    //call for DAO
    public abstract RecipeDAO recipeDAO();
}
