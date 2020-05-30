package com.michaelhsieh.bakingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.michaelhsieh.bakingapp.model.Recipe;
import com.michaelhsieh.bakingapp.model.Step;

import java.util.ArrayList;

/** This Activity uses a master/detail flow to change its display depending on device screen size.
 *
 * On smaller devices, this will display a one-pane layout consisting of the recipe steps list only.
 * On larger devices like tablets, this will display a two-pane layout. That is,
 * The recipe steps list will be displayed together with the selected step details.
 */
public class DetailActivity extends AppCompatActivity implements RecipeStepsListFragment.OnRecipeStepClickListener,
        RecipeStepDetailsFragment.OnPrevButtonClickListener, RecipeStepDetailsFragment.OnNextButtonClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();

    // Recipe key when retrieving Intent
    private static final String EXTRA_RECIPE = "Recipe";

    // key to store selected Step index in bundle
    private static final String KEY_LIST_ITEM_INDEX = "list_item_index";

    // key to restore RecipeStepsListFragment after orientation change in bundle
    private static final String KEY_LIST_FRAGMENT = "list_fragment";

    // List of Steps key when sending Intent
    private static final String EXTRA_STEPS = "Steps";
    // index key when sending Intent
    private static final String EXTRA_LIST_ITEM_INDEX = "list_item_index";

    // the Recipe that was selected from MainActivity
    Recipe recipe;

    // list index of the selected recipe step
    // default value is 0
    private int stepIndex;

    // ArrayList to hold the List of Steps
    ArrayList<Step> steps;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean isTwoPane = false;

    /* Store the RecipeStepsListFragment that's created in two-pane layout.
    The recipe step the user selected before an orientation change should be
    highlighted, which requires this Fragment's highlighted position to be saved.
     */
    private RecipeStepsListFragment recipeStepsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // enable the up button so users can navigate back from this screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "action bar is null");
        }

        // get the Recipe from the Intent that started this Activity
        Intent intent = getIntent();
        recipe = intent.getParcelableExtra(EXTRA_RECIPE);

        // get the selected step index and recipe steps list from orientation change
        if (savedInstanceState != null) {
            stepIndex = savedInstanceState.getInt(KEY_LIST_ITEM_INDEX);

            // Restore the RecipeStepsListFragment's instance
            recipeStepsListFragment = (RecipeStepsListFragment) getSupportFragmentManager().getFragment(savedInstanceState, KEY_LIST_FRAGMENT);
        }


        // determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.two_pane_linear_layout) != null) {
            // this LinearLayout will only initially exist in the two-pane tablet case
            isTwoPane = true;

            if (recipe != null) {

                /* Automatically highlight the first item in two-pane layout,
                since that item is pre-selected by default.
                When the user selects a different recipe step in the list, that step will
                be highlighted instead.
                */

                // create the recipe steps list
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (recipeStepsListFragment == null) {
                    // create new steps list fragment
                    recipeStepsListFragment = RecipeStepsListFragment.newInstance(recipe, isTwoPane);
                }
                // else
                    // using stored steps list fragment
                fragmentTransaction.replace(R.id.recipe_steps_list_container, recipeStepsListFragment);
                fragmentTransaction.commit();

                // start with the first step at position 0
                // create a new ArrayList using the List of Steps
                steps = new ArrayList<Step>(recipe.getSteps());

                /* Old fragments are retained on orientation change because Activity uses
                onSavedInstanceState to save state.

                If the fragment does not exist, create a new RecipeStepDetailsFragment.
                Otherwise, reuse the existing fragment.
                 */
                if (savedInstanceState == null) {

                    /* If you don't check savedInstanceState == null and just replace with new Fragment,
                    then when the screen is rotated, RecipeStepDetailsFragment is created twice.
                    The first Fragment will initialize ExoPlayer then call the Fragment's onDestroy,
                    skipping onPause and onStop. That means ExoPlayer will not be released in onPause.
                    Once the screen is rotated several times, an ExoPlayer Out Of Memory error will occur
                    and the app will crash.
                     */

                    // In two-pane mode, add initial RecipeStepDetailsFragment to the screen
                    fragmentTransaction = fragmentManager.beginTransaction();
                    // use newInstance method instead of constructor, passing in the list of steps and selected step index
                    Fragment recipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, isTwoPane);
                    fragmentTransaction.replace(R.id.recipe_step_details_container, recipeStepDetailsFragment);
                    fragmentTransaction.commit();
                }
                // else
                    // do nothing - fragment is recreated automatically
                    // recipe step details fragment is being reused
            }
            else {
                Log.e(TAG, "recipe retrieved from intent was null");
            }
        }
        else {
            // create the recipe steps list in single-pane layout
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (recipeStepsListFragment == null) {
                // create new steps list fragment
                recipeStepsListFragment = RecipeStepsListFragment.newInstance(recipe, isTwoPane);
            }
            // else
                // using stored steps list fragment
            fragmentTransaction.replace(R.id.recipe_steps_list_container, recipeStepsListFragment);
            fragmentTransaction.commit();
        }

    }

    // Define the behavior for onRecipeStepSelected
    @Override
    public void onRecipeStepSelected(int position) {
        // create a new ArrayList using the List of Steps
        steps = new ArrayList<Step>(recipe.getSteps());

        stepIndex = position;

        // Handle the two-pane case and replace existing fragment right when a new step is selected from the master list
        if (isTwoPane) {
            // Create two-pane interaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepDetailsFragment recipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, isTwoPane);

            // Replace the old recipe step details fragment with a new one
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_details_container, recipeStepDetailsFragment)
                    .commit();

        } else {
            // Handle the single-pane phone case by passing the list of Steps and
            // index in Extras attached to an Intent

            // launch the recipe step details screen only if single-pane
            Intent launchStepDetailsActivity = new Intent(this, RecipeStepDetailsActivity.class);
            launchStepDetailsActivity.putExtra(EXTRA_STEPS, steps);
            launchStepDetailsActivity.putExtra(EXTRA_LIST_ITEM_INDEX, stepIndex);
            startActivity(launchStepDetailsActivity);
        }
    }

    /* prev button should not appear in two-pane layout, so do nothing on click */
    @Override
    public void onPrevButtonClicked(int position) {

    }

    /* next button should not appear in two-pane layout, so do nothing on click */
    @Override
    public void onNextButtonClicked(int position) {

    }

    /**
     * Save the state this Activity, ex. on orientation change
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

        // save the selected step index, ex. on orientation change
        outState.putInt(KEY_LIST_ITEM_INDEX, stepIndex);

        /* Save the RecipeStepsListFragment's instance
        because the position that the user selected right before
        any orientation change should be saved.
        The position is used to highlight the user's selected step in two-pane layout. */
        getSupportFragmentManager().putFragment(outState, KEY_LIST_FRAGMENT, recipeStepsListFragment);
    }
}
