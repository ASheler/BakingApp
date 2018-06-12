package com.glaserproject.bakingapp.RvAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.R;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;

    public void setRecipes (List<Recipe> recipes){
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_tile_main, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (recipes == null){
            return 0;
        }
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.name_tv);

        }

        void bind (int index){
            nameTV.setText(recipes.get(index).name);
        }



    }


}
