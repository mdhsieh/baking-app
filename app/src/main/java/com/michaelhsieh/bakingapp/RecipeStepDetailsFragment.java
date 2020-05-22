package com.michaelhsieh.bakingapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.michaelhsieh.bakingapp.model.Step;


/** This fragment displays a selected recipe step's video
 * and a more detailed description of the step.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeStepDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepDetailsFragment extends Fragment {

    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();

    // parameter argument with name that matches
    // the fragment initialization parameters
    private static final String ARG_STEP = "Step";

    // parameter of type Step
    private Step step;

    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoplayer;

    public RecipeStepDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param step The step to get video and description from.
     * @return A new instance of fragment RecipeStepDetailsFragment.
     */
    public static RecipeStepDetailsFragment newInstance(Step step) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(ARG_STEP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);

        TextView descriptionDisplay = rootView.findViewById(R.id.tv_step_long_desc);
        descriptionDisplay.setText(step.getDescription());

        // Initialize the player view.
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        // Initialize the player.
        String videoUrl = step.getVideoURL();
        /* In the online JSON data, step 5 of Nutella Pie has its video URL misplaced
        in the thumbnail URL instead. Use this thumbnail URL if it's available and
        the video URL is not available.
         */
        String thumbnailURL = step.getThumbnailURL();
        Log.d(TAG, "video url is: " + videoUrl);
        if (videoUrl.isEmpty() && thumbnailURL.isEmpty()) {
            Log.d(TAG, "empty url");
            mPlayerView.setVisibility(View.GONE);
        }
        else if (videoUrl.isEmpty() && !thumbnailURL.isEmpty()) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(thumbnailURL));
            Log.d(TAG, "initializing ExoPlayer with thumbnail URL");
        }
        else {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoUrl));
            Log.d(TAG, "initialized ExoPlayer");
        }

        return rootView;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri)
    {
        if (mExoplayer == null)
        {

            if (getContext() == null) {
                Log.e(TAG, "null returned from getContext()");
                throw new RuntimeException("null returned from getContext()");
            }

            // Create an instance of the Exoplayer
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoplayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoplayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoplayer.prepare(mediaSource);
            mExoplayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer()
    {
        if (mExoplayer != null) {
            mExoplayer.stop();
            mExoplayer.release();
            mExoplayer = null;
        }
    }

    /* Before API 24 you need to release ExoPlayer and other resources in onPause because there is
     no guarantee that onStop is called at all.
     After API 24 you want to release the player in onStop, because onPause may be called when your
     app is still visible in split screen.
     onDestroy may not be called at all and should not be used for releasing resources.
     */
    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        Log.d(TAG, "released ExoPlayer");
    }
}
