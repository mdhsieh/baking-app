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
import java.util.List;

/** This Activity is only launched with small screen sizes.
 * It displays the selected recipe step details in a new screen.
 *
 */
public class RecipeStepDetailsActivity extends AppCompatActivity implements
        RecipeStepDetailsFragment.OnPrevButtonClickListener, RecipeStepDetailsFragment.OnNextButtonClickListener {

    private static final String TAG = RecipeStepDetailsActivity.class.getSimpleName();

    // keys to store List of Steps and selected index in bundle
    private static final String KEY_STEPS = "Steps";
    private static final String KEY_LIST_ITEM_INDEX = "list_item_index";

    // List of Steps key when retrieving Intent
    private static final String EXTRA_STEPS = "Steps";
    // index key when retrieving Intent
    private static final String EXTRA_LIST_ITEM_INDEX = "list_item_index";

    //private static final String STEP_DETAILS_FRAGMENT_TAG = "recipe_step_details_fragment";

    // list of steps to create step details from
    private ArrayList<Step> steps;
    // list index of selected step
    private int stepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        // get the ArrayList of Steps from the Intent that started this Activity
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            steps = savedInstanceState.getParcelableArrayList(KEY_STEPS);
            stepIndex = savedInstanceState.getInt(KEY_LIST_ITEM_INDEX);
            Log.d(TAG, "saved state on orientation change, step index is: " + stepIndex);
        } else {
            steps = intent.getParcelableArrayListExtra(EXTRA_STEPS);
            stepIndex = intent.getIntExtra(EXTRA_LIST_ITEM_INDEX, 0);
        }
        if (steps != null) {
            // this Activity will only be created in the single-pane layout, so show buttons
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Fragment fragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, false);
            // fragmentTransaction.replace(R.id.recipe_step_details_container, fragment);
            // fragmentTransaction.commit();

            /* Old fragments are retained on orientation change because Activity uses
            onSavedInstanceState.
            If the fragment does not exist, create a new RecipeStepDetailsFragment.
            Otherwise, reuse the existing fragment with the given tag.
             */
            /*if (savedInstanceState == null) {
                 Fragment fragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, false);
                 fragmentTransaction.replace(R.id.recipe_step_details_container, fragment, STEP_DETAILS_FRAGMENT_TAG);
                 fragmentTransaction.commit();
            } else {
                // do nothing - fragment is recreated automatically
                Fragment fragment = (RecipeStepDetailsFragment) getSupportFragmentManager().findFragmentByTag(STEP_DETAILS_FRAGMENT_TAG);
                if (fragment != null) {
                    Log.d(TAG, "fragment being reused is " + fragment.getTag());
                }
            }*/

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
                 Fragment fragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, false);
                 fragmentTransaction.replace(R.id.recipe_step_details_container, fragment);
                 fragmentTransaction.commit();
            } else {
                // do nothing - fragment is recreated automatically
                Log.d(TAG, "fragment is being reused");
            }
        }
        else {
            Log.e(TAG, "steps retrieved from intent was null");
        }
    }

    @Override
    public void onPrevButtonClicked(int position) {
        // get the prev step's position
        stepIndex = position - 1;
        // create new details fragment if not already at the first step
        if (steps != null && position > 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepDetailsFragment prevRecipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, false);
            // Replace the old recipe step details fragment with a new one
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_details_container, prevRecipeStepDetailsFragment)
                    .commit();
        }
    }

    @Override
    public void onNextButtonClicked(int position) {
        // get the next step's position
        stepIndex = position + 1;
        // create new details fragment if not already at the last step
        if (steps != null && position < steps.size() - 1) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepDetailsFragment nextRecipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, false);
            // Replace the old recipe step details fragment with a new one
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_details_container, nextRecipeStepDetailsFragment)
                    .commit();
        }
    }

    /**
     * Save the state this Activity, ex. on orientation change
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_STEPS, steps);
        outState.putInt(KEY_LIST_ITEM_INDEX, stepIndex);
    }
}
