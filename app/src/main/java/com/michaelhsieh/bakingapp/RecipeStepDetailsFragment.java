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

import java.util.ArrayList;
import java.util.List;


/** Displays a selected recipe step's video and
 * a more detailed description of the step.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeStepDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepDetailsFragment extends Fragment {

    private static final String TAG = RecipeStepDetailsFragment.class.getSimpleName();

    // parameter argument with name that matches
    // the fragment initialization parameters
    private static final String ARG_STEPS = "Steps";
    private static final String ARG_LIST_ITEM_INDEX = "list_item_index";

    // parameter of type List of Steps
    private List<Step> steps = new ArrayList<>();
    // parameter of type int
    private int listItemIndex;

    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoplayer;

    public RecipeStepDetailsFragment() {
        // Required empty public constructor
    }

    /* We are using the whole list of steps in newInstance because we will need to know
     * the next and previous steps when next and previous buttons are clicked.
     */

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param steps The list of steps to get video and description from.
     * @param listItemIndex The index of the selected step.
     * @return A new instance of fragment RecipeStepDetailsFragment.
     */
    public static RecipeStepDetailsFragment newInstance(ArrayList<Step> steps, int listItemIndex) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_STEPS, steps);
        args.putInt(ARG_LIST_ITEM_INDEX, listItemIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            steps = getArguments().getParcelableArrayList(ARG_STEPS);
            listItemIndex = getArguments().getInt(ARG_LIST_ITEM_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);

        TextView descriptionDisplay = rootView.findViewById(R.id.tv_step_long_desc);
        descriptionDisplay.setText(steps.get(listItemIndex).getDescription());

        // Initialize the player view.
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        // Initialize the player.
        String videoUrl = steps.get(listItemIndex).getVideoURL();
        /* In the online JSON data, step 5 of Nutella Pie has its video URL misplaced
        in the thumbnail URL instead. Use this thumbnail URL if it's available and
        the video URL is not available.
         */
        String thumbnailURL = steps.get(listItemIndex).getThumbnailURL();
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

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void setListItemIndex(int listItemIndex) {
        this.listItemIndex = listItemIndex;
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
     app is still visible in split screen. onPause and onStop are guaranteed to be called.

     onDestroy may not be called at all and should not be used for releasing resources.
     */
    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        Log.d(TAG, "released ExoPlayer");
    }
}
