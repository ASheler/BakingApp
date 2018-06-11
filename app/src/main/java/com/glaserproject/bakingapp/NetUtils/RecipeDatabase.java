package com.glaserproject.bakingapp.NetUtils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.glaserproject.bakingapp.Objects.Recipe;

@Database(entities = {Recipe.class}, version = 2, exportSchema = false)
@TypeConverters(ListConverter.class)
public abstract class RecipeDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DB_NAME = "recipes";
    private static RecipeDatabase sInstance;

    public static RecipeDatabase getInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class,
                        DB_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract RecipeDAO recipeDAO();
}
