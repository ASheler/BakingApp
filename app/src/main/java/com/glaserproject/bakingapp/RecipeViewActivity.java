package com.glaserproject.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.Fragments.StepSelectionFragment;
import com.glaserproject.bakingapp.Fragments.StepViewFragment;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;

public class RecipeViewActivity extends AppCompatActivity implements StepSelectionFragment.OnStepClickListener {

    private boolean mTwoPane;
    static Recipe recipe;
    Step step;
    int stepId;


    //send Recipe to Master Fragment - Always connected
    public static Recipe getRecipe() {
        return recipe;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get Recipe from Intent
        Intent intent = getIntent();
        recipe = intent.getParcelableExtra(AppConstants.RECIPE_EXTRA_KEY);
        if (recipe == null) {
            finish();
        }

        //set Content view for Fragment
        setContentView(R.layout.activity_recipe_view);


        //Check if Two pane layout
        if (findViewById(R.id.wide_lin_layout) != null) {
            mTwoPane = true;

            //init zero step ID for first call
            step = recipe.steps.get(stepId);

            //put step into bundle for Fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, step);

            //init Fragment and insert Bundle
            FragmentManager fragmentManager = getSupportFragmentManager();
            StepViewFragment stepViewFragment = new StepViewFragment();
            stepViewFragment.setArguments(bundle);

            //fire up Fragment
            fragmentManager.beginTransaction()
                    .add(R.id.step_view_layout, stepViewFragment)
                    .commit();

        } else {
            mTwoPane = false;
        }

    }


    //On Click on step in Master Fragmnet
    @Override
    public void onStepClick(Step step) {

        //Check if we have Two pane - if so, we have to set up new fragment.
        //If no, we have to fire up new Activity
        if (mTwoPane) {
            //change the fragment source

            //put new step into Bundle
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, step);

            //setup Fragment and add bundle
            FragmentManager fragmentManager = getSupportFragmentManager();
            StepViewFragment stepViewFragment = new StepViewFragment();
            stepViewFragment.setArguments(bundle);

            //replace with New Fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.step_view_layout, stepViewFragment)
                    .commit();

        } else {

            //Start new activity with one layout
            Intent intent = new Intent(this, StepViewActivity.class);

            //put step into Bundle
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.STEP_BUNDLE_KEY, step);
            intent.putExtra(AppConstants.STEP_EXTRA_KEY, bundle);
            startActivity(intent);

        }
    }
}
