package com.glaserproject.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.Fragments.StepViewFragment;

public class StepViewActivity extends AppCompatActivity {

    int stepId;
    int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_view);

        //get Step from from incoming intent
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle = intent.getBundleExtra(AppConstants.STEP_EXTRA_KEY);

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


}
