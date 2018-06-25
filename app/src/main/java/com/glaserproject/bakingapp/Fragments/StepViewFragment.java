package com.glaserproject.bakingapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.glaserproject.bakingapp.AppConstants.AppConstants;
import com.glaserproject.bakingapp.Objects.Recipe;
import com.glaserproject.bakingapp.Objects.Step;
import com.glaserproject.bakingapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class StepViewFragment extends Fragment implements Player.EventListener {

    Step step;
    Recipe recipe;
    OnPrevClickListener mPrevCallback;
    OnNextClickListener mNextCallback;
    SimpleExoPlayer mExoPlayer;
    Handler mainHandler;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;

    @BindView(R.id.next_step_tv)
    TextView nextStepTV;
    @BindView(R.id.previous_step_tv)
    TextView previousStepTV;
    @BindView(R.id.description_tv)
    TextView description_tv;
    @BindView(R.id.step_video_exoplayer)
    PlayerView mPlayerView;
    @BindView(R.id.mediaCard)
    CardView mediaCard;


    public StepViewFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //attach callbacks for activity
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
            initMediaSession();

        }



        return rootView;
    }

    public void initMediaSession() {
        //add listener for media Session
        mExoPlayer.addListener(this);

        //init media session to handle media buttons
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE
                );
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    public void playVideo() {

        if (mExoPlayer == null) {

            //load track selector, load control, rendersFactory - all default
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());

            //init instance
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            //put player into view
            mPlayerView.setPlayer(mExoPlayer);

            //get video URL and parse it to Uri
            String videoUrl = step.videoURL;
            Uri mp4VideoUri = Uri.parse(videoUrl);

            //get UserAgent and setup data Source Factory with it
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), userAgent);

            ExtractorMediaSource.Factory mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory);

            //init actual playback and run when ready
            mExoPlayer.prepare(mediaSource.createMediaSource(mp4VideoUri));
            mPlayerView.hideController();
            mExoPlayer.setPlayWhenReady(true);
        }

    }

    public void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
        mMediaSession.release();
        mPlayerView = null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getCurrentPosition(), 1f);

        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getCurrentPosition(), 1f);

        }

        mMediaSession.setPlaybackState(mStateBuilder.build());
        Log.d("HOVNOOOO", "Playback State Changed");
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    //navigation buttons interfaces
    public interface OnPrevClickListener {
        void onPrevClick(int prevStepId);
    }

    public interface OnNextClickListener {
        void onNextClick(int nextStepId);
    }

    //TODO: ON ROTATION BACK TO 1


    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
            Log.d("HOVNOOOO", "Play Pressed");

        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
            Log.d("HOVNOOOO", "Pause Pressed");
        }
    }
}
