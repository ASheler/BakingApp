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
    private final RecipesAdapterOnClickHandler mClickHandler;

    //init with ClickHandler
    public RecipesAdapter(RecipesAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    //set recipes into adapter
    public void setRecipes (List<Recipe> recipes){
        this.recipes = recipes;
        //notify adapter of chnage
        notifyDataSetChanged();
    }

    //Click Interface
    public interface RecipesAdapterOnClickHandler {
        void onClick(int recipeId);
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

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTV;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.name_tv);

            itemView.setOnClickListener(this);

        }

        void bind (int index){
            nameTV.setText(recipes.get(index).name);
        }


        @Override
        public void onClick(View v) {

            //get ID of item, not adapter position
            mClickHandler.onClick(recipes.get(getAdapterPosition()).id);

        }
    }


}
