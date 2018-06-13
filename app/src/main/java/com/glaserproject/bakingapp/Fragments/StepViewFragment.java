package com.glaserproject.bakingapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.R;

public class StepViewFragment extends Fragment {

    Step step;


    public StepViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_view, container, false);

        //get Step from Bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            step = bundle.getParcelable(AppConstants.STEP_BUNDLE_KEY);
        }


        //Setup views from Step
        TextView textView = rootView.findViewById(R.id.textView2);
        textView.setText(step.description);

        return rootView;
    }


}
