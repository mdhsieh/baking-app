package com.michaelhsieh.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private static final String ARG_HIDE_BUTTONS = "hide_buttons";

    // parameter of type List of Steps
    private List<Step> steps = new ArrayList<>();
    // parameter of type int
    private int listItemIndex;
    // parameter of type boolean to hide buttons in single-pane and show buttons in two-pane case
    private boolean hideButtons;

    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoplayer;

    private Button prevButton;
    private Button nextButton;

    // Define a new interface OnPrevButtonClickListener that triggers a callback in the host activity,
    // which is DetailActivity
    private OnPrevButtonClickListener prevCallback;

    // OnPrevButtonClickListener interface, calls a method in the host activity named onPrevButtonClicked

    /* onPrevButtonClicked will change the step details fragment to match the previous step,
    or do nothing if already at the first step. */
    public interface OnPrevButtonClickListener
    {
        void onPrevButtonClicked(int position);
    }

    // Define a new interface OnNextButtonClickListener that triggers a callback in the host activity
    private OnNextButtonClickListener nextCallback;

    // OnNextButtonClickListener interface, calls a method in the host activity named onNextButtonClicked

    /* onNextButtonClicked will change the step details fragment to match the next step,
    or do nothing if already at the last step. */
    public interface OnNextButtonClickListener
    {
        void onNextButtonClicked(int position);
    }

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
     * @param hideButtons Whether or not to display next and previous buttons.
     *                    Single-pane shows buttons, two-pane does not.
     * @return A new instance of fragment RecipeStepDetailsFragment.
     */
    public static RecipeStepDetailsFragment newInstance(ArrayList<Step> steps, int listItemIndex, boolean hideButtons) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_STEPS, steps);
        args.putInt(ARG_LIST_ITEM_INDEX, listItemIndex);
        args.putBoolean(ARG_HIDE_BUTTONS, hideButtons);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            prevCallback = (OnPrevButtonClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnPrevButtonClickListener");
        }

        try {
            nextCallback = (OnNextButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnNextButtonClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            steps = getArguments().getParcelableArrayList(ARG_STEPS);
            listItemIndex = getArguments().getInt(ARG_LIST_ITEM_INDEX);
            hideButtons = getArguments().getBoolean(ARG_HIDE_BUTTONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);

        // set the description
        TextView descriptionDisplay = rootView.findViewById(R.id.tv_step_long_desc);
        descriptionDisplay.setText(steps.get(listItemIndex).getDescription());

        // Initialize the player view.
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        // release player and initialize again if ex. orientation changes
        // because don't want same audio playing twice
        /*if (mExoplayer != null)
        {
            Log.d(TAG, "ExoPlayer is not null in onCreate, releasing player");
            mExoplayer.release();
        }*/

        // Initialize the player.
        // get selected step's video URL
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

        // get the previous and next buttons
        prevButton = rootView.findViewById(R.id.btn_prev);
        nextButton = rootView.findViewById(R.id.btn_next);

        // show buttons in single-pane layout, but not two-pane layout
        // since tablet can just navigate to another step's detail on same screen
        if (!hideButtons) {
            prevButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }
        else {
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }

        // if step is already the first step, don't show the prev button
        if (listItemIndex == 0)
        {
            prevButton.setVisibility(View.GONE);
        }
        // if step is already the last step, don't show the next button
        else if (steps!= null && listItemIndex == steps.size()-1)
        {
            nextButton.setVisibility(View.GONE);
        }

        // the next and previous buttons trigger callbacks to host activity
        prevButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // trigger the callback onPrevButtonClicked when the prev button is clicked
                prevCallback.onPrevButtonClicked(listItemIndex);
            }
        });

        nextButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // trigger the callback onPrevButtonClicked when the prev button is clicked
                nextCallback.onNextButtonClicked(listItemIndex);
            }
        });

        return rootView;
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
