package com.glaserproject.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.Fragments.StepViewFragment;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;

public class StepViewActivity extends AppCompatActivity implements StepViewFragment.OnPrevClickListener, StepViewFragment.OnNextClickListener {

    Recipe recipe;
    Step step;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_view);

        if (savedInstanceState == null) {
            //get Step from from incoming intent
            Intent intent = getIntent();
            bundle = intent.getBundleExtra(AppConstants.BUNDLE_EXTRA_KEY);


        } else {
            bundle = savedInstanceState.getBundle(AppConstants.SAVED_INSTANCE_BUNDLE_KEY);
        }

        //get Step and Recipe for next use after click
        step = bundle.getParcelable(AppConstants.STEP_BUNDLE_KEY);
        recipe = bundle.getParcelable(AppConstants.RECIPE_BUNDLE_KEY);


        //set name to Action Bar
        getSupportActionBar().setTitle(recipe.name);


        //Setup Fragment
        StepViewFragment stepViewFragment = new StepViewFragment();
        //send Step to Fragment
        stepViewFragment.setArguments(bundle);

        //Fire up FragManager and show Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.step_view_layout, stepViewFragment)
                .commit();
    }


    @Override
    public void onPrevClick(int prevStepId) {
        StepViewFragment stepViewFragment = new StepViewFragment();

        Step prevStep = recipe.steps.get(prevStepId);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, prevStep);
        bundle.putParcelable(AppConstants.RECIPE_BUNDLE_KEY, recipe);

        stepViewFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_view_layout, stepViewFragment)
                .commit();
    }

    @Override
    public void onNextClick(int nextStepId) {
        StepViewFragment stepViewFragment = new StepViewFragment();

        Step nextStep = recipe.steps.get(nextStepId);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, nextStep);
        bundle.putParcelable(AppConstants.RECIPE_BUNDLE_KEY, recipe);

        stepViewFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_view_layout, stepViewFragment)
                .commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(AppConstants.SAVED_INSTANCE_BUNDLE_KEY, bundle);
    }
}
