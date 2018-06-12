package com.glaserproject.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.Fragments.StepSelectionFragment;
import com.glaserproject.bakingapp.Fragments.StepViewFragment;

public class RecipeViewActivity extends AppCompatActivity implements StepSelectionFragment.OnStepClickListener {

    static int recipeId;
    private boolean mTwoPane;

    public static int getRecipeId() {
        return recipeId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        recipeId = intent.getIntExtra(AppConstants.SELECTED_RECIPE_EXTRA_KEY, 999);
        if (recipeId == 999) {
            finish();
        }
        setContentView(R.layout.activity_recipe_view);


        if (findViewById(R.id.wide_lin_layout) != null) {
            mTwoPane = true;

            FragmentManager fragmentManager = getSupportFragmentManager();
            StepViewFragment stepViewFragment = new StepViewFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.step_view_layout, stepViewFragment)
                    .commit();

        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onStepClick(int stepId) {
        if (mTwoPane) {
            //change the fragment source
            Toast.makeText(this, "Item clicked", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, StepViewActivity.class);
            intent.putExtra(AppConstants.STEP_EXTRA_KEY, stepId);
            startActivity(intent);
            Toast.makeText(this, "" + stepId, Toast.LENGTH_SHORT).show();
        }
    }
}
