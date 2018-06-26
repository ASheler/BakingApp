package com.glaserproject.bakingapp.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.CardView;
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
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class StepViewFragment extends Fragment implements Player.EventListener {

    Step step;
    Recipe recipe;

    SimpleExoPlayer mExoPlayer;
    MediaSessionCompat mMediaSession;
    PlaybackStateCompat.Builder mStateBuilder;

    long mPlayerPosition;
    boolean isTwoPane;

    OnPrevClickListener mPrevCallback;
    OnNextClickListener mNextCallback;
    SavePlayerPosition mSavePlayerPosition;


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
    @BindView(R.id.no_connection_tv)
    TextView noConnectionTV;


    public StepViewFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //attach callbacks for activity
        try {
            mPrevCallback = (OnPrevClickListener) context;
            mNextCallback = (OnNextClickListener) context;
            mSavePlayerPosition = (SavePlayerPosition) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //get Step, recipe and player position from Bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            step = bundle.getParcelable(AppConstants.STEP_BUNDLE_KEY);
            recipe = bundle.getParcelable(AppConstants.RECIPE_BUNDLE_KEY);
            isTwoPane = bundle.getBoolean(AppConstants.IS_TWO_PANE_BUNDLE_KEY);
            mPlayerPosition = bundle.getLong(AppConstants.PLAYER_POSITION_BUNDLE_KEY);

        }
        //get rootView depending if we have video url or not
        View rootView;
        if (step.videoURL.equals("")) {
            //no video url
            rootView = inflater.inflate(R.layout.fragment_step_view, container, false);
        } else {
            //video layout
            rootView = inflater.inflate(R.layout.fragment_step_view_video, container, false);
        }
        //Bind view ButterKnife
        ButterKnife.bind(this, rootView);


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


        //check if we have
        if (step.videoURL.equals("")) {
            //hide whole media card
            mediaCard.setVisibility(View.GONE);
        } else {
            //check connection for video
            if (isNetworkAvailable(getContext())) {
                //play video and initialize media session
                playVideo();
                initMediaSession();
            } else {
                //hide player and show error message
                mPlayerView.setVisibility(View.GONE);
                noConnectionTV.setVisibility(View.VISIBLE);
            }
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        //play video on resume to ensure the player is initialize on resume to activity
        if (!step.videoURL.equals("")) {
            playVideo();
        }
    }

    //media session initialization
    public void initMediaSession() {
        //add listener for media Session
        mExoPlayer.addListener(this);

        //init media session to handle media buttons
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setMediaButtonReceiver(null);

        //set play/pause actions
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE
                );
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    //initialize and play video
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
            //seek to player position = if position not stored - default 0
            mExoPlayer.seekTo(mPlayerPosition);

            //check if the orientation is landscape - if it is, load in fullscreen
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE & !isTwoPane) {
                runFullScreen();
            }

        }

    }

    //releasing player
    public void releasePlayer() {
        if (mExoPlayer != null) {
            //save player position locally to fragment hosting activity
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mSavePlayerPosition.savePlayerPosition(mExoPlayer.getCurrentPosition());

            //release player
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        //release media session
        if (mMediaSession != null) {
            mMediaSession.release();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
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

        //check state of player and build state actions
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            //player is playing
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getCurrentPosition(), 1f);

        } else if ((playbackState == Player.STATE_READY)) {
            //player is paused
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getCurrentPosition(), 1f);

        }

        mMediaSession.setPlaybackState(mStateBuilder.build());
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


    //Check if Network connection is available
    public boolean isNetworkAvailable(Context context) {
        //set connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //check if ActiveNetwork isn't null && is Connected
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    //run player in fullscreen
    private void runFullScreen() {
        //get DecorView
        View decorView = getActivity().getWindow().getDecorView();
        //set immersive flags
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    //save player position to fragment hosting activity
    public interface SavePlayerPosition {
        void savePlayerPosition(long playerPosition);
    }


    //callback for Media Session
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
    }
}
