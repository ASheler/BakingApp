package com.glaserproject.bakingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.NetUtils.AppExecutors;
import com.glaserproject.bakingapp.NetUtils.RecipeDatabase;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepViewFragment extends Fragment {

    Step step;
    Recipe recipe;
    OnPrevClickListener mPrevCallback;
    OnNextClickListener mNextCallback;
    SimpleExoPlayer mExoPlayer;
    Handler mainHandler;

    @BindView(R.id.next_step_tv)
    TextView nextStepTV;
    @BindView(R.id.previous_step_tv)
    TextView previousStepTV;
    @BindView(R.id.description_tv)
    TextView description_tv;
    @BindView(R.id.step_video_exoplayer)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.mediaCard)
    CardView mediaCard;


    public StepViewFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mPrevCallback = (OnPrevClickListener) context;
            mNextCallback = (OnNextClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Listener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_view, container, false);

        //Bind view ButterKnife
        ButterKnife.bind(this, rootView);

        //get Step from Bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            step = bundle.getParcelable(AppConstants.STEP_BUNDLE_KEY);
            recipe = bundle.getParcelable(AppConstants.RECIPE_BUNDLE_KEY);
        }

        mainHandler = new Handler();

        //Setup views from Step

        description_tv.setText(step.description);

        //hide next/prev step if not available
        if (step.id == 0) {
            previousStepTV.setVisibility(View.INVISIBLE);
        } else if (step.id == recipe.steps.size() - 1) {
            nextStepTV.setVisibility(View.INVISIBLE);
        }


        //PreviousStepButton
        previousStepTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrevCallback.onPrevClick(step.id - 1);

            }
        });

        //NextStepButton

        nextStepTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNextCallback.onNextClick(step.id + 1);
            }
        });

        if (step.videoURL.equals("")) {
            mediaCard.setVisibility(View.GONE);
        } else {
            playVideo();
        }



        return rootView;
    }

    public void playVideo() {

        if (mExoPlayer == null) {

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            String videoUrl = step.videoURL;

            Uri mp4VideoUri = Uri.parse(videoUrl);

            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mp4VideoUri, new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }

    }

    public void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onStop() {
        if (mExoPlayer != null) {
            releasePlayer();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    public interface OnPrevClickListener {
        void onPrevClick(int prevStepId);
    }

    public interface OnNextClickListener {
        void onNextClick(int nextStepId);
    }

    //TODO: ON ROTATION BACK TO 1
}
