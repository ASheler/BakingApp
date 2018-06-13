package com.glaserproject.bakingapp.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.R;
import com.glaserproject.bakingapp.RecipeViewActivity;
import com.glaserproject.bakingapp.RvAdapters.StepsAdapter;
import com.glaserproject.bakingapp.RvAdapters.StepsAdapter1;


public class StepSelectionFragment extends Fragment implements StepsAdapter1.StepsAdapterOnClickHandler {

    OnStepClickListener mCallback;
    RecyclerView stepsListRV;
    StepsAdapter1 mAdapter;
    RecipeDatabase mDb;

    //mandatory empty builder
    public StepSelectionFragment() {
    }

    //onClic - sends callback to Activity
    @Override
    public void onClick(int stepId) {
        mCallback.onStepClick(stepId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //attach callback to fragment
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Listener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_selection, container, false);

        //init db
        mDb = RecipeDatabase.getInstance(getActivity());

        //init recyclerView
        stepsListRV = rootView.findViewById(R.id.step_selection_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        stepsListRV.setLayoutManager(layoutManager);

        //setup adapter with clickHandler
        mAdapter = new StepsAdapter1(this);

        stepsListRV.setAdapter(mAdapter);

        //get recipes from Db
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                Recipe recipe = mDb.recipeDAO().loadRecipe(RecipeViewActivity.getRecipeId());
                mAdapter.setRecipe(recipe);
            }
        });

        return rootView;
    }

    public interface OnStepClickListener {
        void onStepClick(int stepId);
    }


}
