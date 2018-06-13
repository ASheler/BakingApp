package com.glaserproject.bakingapp.Objects;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.glaserproject.bakingapp.NetUtils.ListConverter;

import java.util.ArrayList;
import java.util.List;

//set up table via ROOM
@Entity(tableName = "recipes")
public class Recipe implements Parcelable {

    //Parcel Creator
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };


    //Vars & columns
    @PrimaryKey(autoGenerate = false)
    public int id;
    public String name;
    @TypeConverters(ListConverter.class)
    public List<Ingredient> ingredients = new ArrayList<Ingredient>();
    @TypeConverters(ListConverter.class)
    public List<Step> steps = new ArrayList<Step>();
    public int servings;
    public String image;


    //primary init
    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }


    public List<Step> getSteps() {
        return this.steps;
    }


    //read from parcel
    public Recipe(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        in.readTypedList(ingredients, Ingredient.CREATOR);
        in.readTypedList(steps, Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //write into parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredients);
        dest.writeTypedList(this.steps);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }
}
