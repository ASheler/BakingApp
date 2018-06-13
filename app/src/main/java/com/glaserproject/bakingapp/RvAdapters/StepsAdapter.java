package com.glaserproject.bakingapp.RvAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glaserproject.bakingapp.Fragments.StepSelectionFragment;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.R;

import java.util.List;

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

                //set text
                viewHolder0.ingredientsLabelTV.setText("Ingredients");

                break;
            default:

                ViewHolderX viewHolderX = (ViewHolderX) holder;

                viewHolderX.shortDescriptionTV.setText(steps.get(position - 1).shortDescription);

                break;

        }
    }


    //click interface
    public interface StepsAdapterOnClickHandler {
        void onClick(int stepId);
    }

    //viewHolder for Ingredients
    public class IngredientsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ingredientsLabelTV;

        public IngredientsViewHolder(View itemView) {
            super(itemView);

            ingredientsLabelTV = itemView.findViewById(R.id.ingredients_label_tv);
        }


        @Override
        public void onClick(View v) {
            //TODO: change 2 (placeholder) for actual stepID
            mClickHandler.onClick(2);
        }
    }

    //stepsViewHolder
    class ViewHolderX extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView shortDescriptionTV;

        public ViewHolderX(View itemView) {
            super(itemView);

            shortDescriptionTV = itemView.findViewById(R.id.short_description_tv);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(33);
        }
    }
}
