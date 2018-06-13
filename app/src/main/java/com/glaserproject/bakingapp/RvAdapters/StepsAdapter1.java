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

public class StepsAdapter1 extends RecyclerView.Adapter<StepsAdapter1.ViewHolder> {

    private final StepsAdapterOnClickHandler mClickHandler;
    private Recipe recipe;
    private List<Step> steps;


    //init with Click Handler
    public StepsAdapter1(StepsAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    //set recipe and steps
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        this.steps = recipe.steps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (steps == null) {
            return 0;
        }
        //TODO: check for actual size (+1??)
        return steps.size();
    }

    @NonNull
    @Override
    public StepsAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //setup different layout for first view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view0 = inflater.inflate(R.layout.ingredients_tile, parent, false);
        View view = inflater.inflate(R.layout.step_selection_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }


    //click interface
    public interface StepsAdapterOnClickHandler {
        void onClick(int stepId);
    }

    //stepsViewHolder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView shortDescriptionTV;

        public ViewHolder(View itemView) {
            super(itemView);

            shortDescriptionTV = itemView.findViewById(R.id.short_description_tv);
        }

        void bind(int index) {
            shortDescriptionTV.setText("HOVNO");
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(33);
        }
    }
}
