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

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_tile_main, parent, false);
        //return inflated layout
        return new RecipeViewHolder(view);
    }

    //Click Interface
    public interface RecipesAdapterOnClickHandler {
        void onClick(Recipe recipe);
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

    //ViewHolder Class
    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //init UI variables
        @BindView(R.id.name_tv)
        TextView nameTV;
        @BindView(R.id.servings_number_tv)
        TextView servingsNumberTv;
        @BindView(R.id.steps_number_tv)
        TextView stepsNumberTv;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            //Bind views with butterKnife
            ButterKnife.bind(this, itemView);

            //set Click listeners to ItemView
            itemView.setOnClickListener(this);

        }

        void bind (int index){

            String nOfServings = "" + recipes.get(index).servings;
            String nOfSteps = "" + recipes.get(index).steps.size();

            nameTV.setText(recipes.get(index).name);
            servingsNumberTv.setText(nOfServings);
            stepsNumberTv.setText(nOfSteps);
        }


        @Override
        public void onClick(View v) {

            //get ID of item, not adapter position
            mClickHandler.onClick(recipes.get(getAdapterPosition()));

        }
    }


}
