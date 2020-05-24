package com.michaelhsieh.bakingapp;

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

    // List of Steps key when retrieving Intent
    private static final String EXTRA_STEPS = "Steps";
    // index key when retrieving Intent
    private static final String EXTRA_LIST_ITEM_INDEX = "list_item_index";

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
        steps = intent.getParcelableArrayListExtra(EXTRA_STEPS);
        if (steps != null) {
            // create a new ArrayList using the List of Steps
            //ArrayList<Step> steps = new ArrayList<Step>(stepsList);
            stepIndex = intent.getIntExtra(EXTRA_LIST_ITEM_INDEX, 0);

            // this Activity will only be created in the single-pane layout, so show buttons
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex, false);
            fragmentTransaction.replace(R.id.recipe_step_details_container, fragment);
            fragmentTransaction.commit();
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
        } else {
            Log.d(TAG, "no prev step");
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
        } else {
            Log.d(TAG, "no next step");
        }
    }
}
