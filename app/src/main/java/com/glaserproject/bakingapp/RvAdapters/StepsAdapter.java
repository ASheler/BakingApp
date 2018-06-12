package com.glaserproject.bakingapp.RvAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.R;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private final StepsAdapterOnClickHandler mClickHandler;
    private List<Step> steps;
    private Recipe recipe;


    //init with ClickHandler
    public StepsAdapter(StepsAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        this.steps = recipe.steps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.step_selection_tile, parent, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (steps == null) {
            return 0;
        }
        return steps.size();
    }

    //Click interface
    public interface StepsAdapterOnClickHandler {
        void onClick(int stepId);
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView shortDescriptionTV;

        public StepsViewHolder(View itemView) {
            super(itemView);

            shortDescriptionTV = itemView.findViewById(R.id.short_description_tv);

            itemView.setOnClickListener(this);
        }

        void bind(int index) {
            if (index == 0) {
                shortDescriptionTV.setText(recipe.name);
            } else {
                shortDescriptionTV.setText(steps.get(index).shortDescription);
            }
        }

        @Override
        public void onClick(View v) {
            //get ID of item clicked
            mClickHandler.onClick(steps.get(getAdapterPosition()).id);
        }
    }
}
