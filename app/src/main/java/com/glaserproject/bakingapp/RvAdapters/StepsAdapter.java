package com.glaserproject.bakingapp.RvAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glaserproject.bakingapp.Fragments.StepSelectionFragment;
import com.glaserproject.bakingapp.Objects.Ingredient;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final StepsAdapterOnClickHandler mClickHandler;
    private Recipe recipe;
    private List<Step> steps;


    //init with Click Handler
    public StepsAdapter(StepsAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    //set recipe and steps
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        this.steps = recipe.steps;
        notifyDataSetChanged();
    }

    //get position as ViewType for distinguishing between first View and others
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (steps == null) {
            return 0;
        }

        return steps.size() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //setup different layout for first view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view0 = inflater.inflate(R.layout.ingredients_tile, parent, false);
        View view = inflater.inflate(R.layout.step_selection_tile, parent, false);


        switch (viewType) {
            case 0:
                return new IngredientsViewHolder(view0);
            default:
                return new ViewHolderX(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                IngredientsViewHolder viewHolder0 = (IngredientsViewHolder) holder;

                //build text
                String ingredients = ingredientsTextBuilder();

                //set text
                viewHolder0.ingredientsListTV.setText(ingredients);

                break;
            default:

                ViewHolderX viewHolderX = (ViewHolderX) holder;

                viewHolderX.shortDescriptionTV.setText(steps.get(position - 1).shortDescription);

                break;

        }
    }


    //click interface
    public interface StepsAdapterOnClickHandler {
        void onClick(Step step);
    }

    public String ingredientsTextBuilder() {
        String ingredients;

        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingredient : recipe.ingredients) {
            stringBuilder.append(ingredient.getIngredient());
            stringBuilder.append(" - ");
            stringBuilder.append(ingredient.getQuantity());
            stringBuilder.append(" ");
            stringBuilder.append(ingredient.getMeasure());
            stringBuilder.append("\n \n");
        }

        ingredients = stringBuilder.toString();

        return ingredients;
    }

    //viewHolder for Ingredients
    public class IngredientsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredients_list_tv)
        TextView ingredientsListTV;

        public IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    //stepsViewHolder
    class ViewHolderX extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.short_description_tv)
        TextView shortDescriptionTV;

        public ViewHolderX(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(steps.get(getAdapterPosition() - 1));
        }
    }

}
