package com.glaserproject.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.Fragments.StepViewFragment;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;

public class StepViewActivity extends AppCompatActivity implements
        StepViewFragment.OnPrevClickListener,
        StepViewFragment.OnNextClickListener,
        StepViewFragment.SavePlayerPosition {

    Recipe recipe;
    Step step;
    Bundle bundle;
    long playerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //get Step from from incoming intent
            Intent intent = getIntent();
            bundle = intent.getBundleExtra(AppConstants.BUNDLE_EXTRA_KEY);
            step = bundle.getParcelable(AppConstants.STEP_BUNDLE_KEY);
            recipe = bundle.getParcelable(AppConstants.RECIPE_BUNDLE_KEY);

        } else {
            //or get data from savedState
            bundle = savedInstanceState.getBundle(AppConstants.SAVED_INSTANCE_BUNDLE_KEY);
            playerPosition = savedInstanceState.getLong(AppConstants.SAVED_INSTANCE_ACTIVITY_PLAYER_POSITION);
            step = bundle.getParcelable(AppConstants.STEP_BUNDLE_KEY);
            recipe = bundle.getParcelable(AppConstants.RECIPE_BUNDLE_KEY);
        }


        setContentView(R.layout.activity_step_view);


        //get Step and Recipe for next use after click

        bundle.putLong(AppConstants.PLAYER_POSITION_BUNDLE_KEY, playerPosition);


        //set name to Action Bar
        getSupportActionBar().setTitle(recipe.name);


        //Setup Fragment
        StepViewFragment stepViewFragment = new StepViewFragment();
        //send Step to Fragment
        stepViewFragment.setArguments(bundle);

        //Fire up FragManager and show Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_view_layout, stepViewFragment)
                .commit();
    }


    @Override
    public void onPrevClick(int prevStepId) {

        //init fragment
        StepViewFragment stepViewFragment = new StepViewFragment();

        //get step
        Step prevStep = recipe.steps.get(prevStepId);
        step = prevStep;

        //put into bundle
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, prevStep);
        bundle.putParcelable(AppConstants.RECIPE_BUNDLE_KEY, recipe);

        //put into fragment
        stepViewFragment.setArguments(bundle);

        //replace fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_view_layout, stepViewFragment)
                .commit();
    }

    @Override
    public void onNextClick(int nextStepId) {
        StepViewFragment stepViewFragment = new StepViewFragment();

        //get step
        Step nextStep = recipe.steps.get(nextStepId);
        step = nextStep;

        //put into bundle
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, nextStep);
        bundle.putParcelable(AppConstants.RECIPE_BUNDLE_KEY, recipe);

        //put into fragment
        stepViewFragment.setArguments(bundle);

        //replace fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_view_layout, stepViewFragment)
                .commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, step);
        outState.putBundle(AppConstants.SAVED_INSTANCE_BUNDLE_KEY, bundle);
        outState.putLong(AppConstants.SAVED_INSTANCE_ACTIVITY_PLAYER_POSITION, playerPosition);
    }

    @Override
    public void savePlayerPosition(long savePlayerPosition) {
        playerPosition = savePlayerPosition;
    }
}

