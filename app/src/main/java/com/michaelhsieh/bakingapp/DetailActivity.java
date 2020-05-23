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

/** This Activity uses a master/detail flow to change its display depending on device screen size.
 *
 * On smaller devices, this will display the recipe steps list only.
 * On larger devices like tablets, this will display a two-pane layout. That is,
 * The recipe steps list will be displayed together with the selected step details.
 */
public class DetailActivity extends AppCompatActivity implements RecipeStepsListFragment.OnRecipeStepClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();

    // Recipe key when retrieving Intent
    private static final String EXTRA_RECIPE = "Recipe";

    // List of Steps key when sending Intent
    private static final String EXTRA_STEPS = "Steps";
    // index key when sending Intent
    private static final String EXTRA_LIST_ITEM_INDEX = "list_item_index";

    // the Recipe that was selected from MainActivity
    Recipe recipe;

    // list index of the selected recipe step
    // default value is 0
    private int stepIndex;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get the Recipe from the Intent that started this Activity
        Intent intent = getIntent();
        recipe = intent.getParcelableExtra(EXTRA_RECIPE);

        // create the recipe steps list
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = RecipeStepsListFragment.newInstance(recipe);
        fragmentTransaction.replace(R.id.recipe_steps_list_container, fragment);
        fragmentTransaction.commit();*/

        // create the recipe steps list
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RecipeStepsListFragment recipeStepsListFragment = RecipeStepsListFragment.newInstance(recipe);
        recipeStepsListFragment.activateItemHighlighting();
        fragmentTransaction.replace(R.id.recipe_steps_list_container, recipeStepsListFragment);
        fragmentTransaction.commit();


        // determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.two_pane_linear_layout) != null) {
            // this LinearLayout will only initially exist in the two-pane tablet case
            isTwoPane = true;

            if (recipe != null) {

                // create the recipe steps list

                /* Automatically highlight the first item in two-pane layout,
                since that item is pre-selected by default.
                When the user selects a different recipe step in the list, that step will
                be highlighted instead.
                */
                /*FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RecipeStepsListFragment recipeStepsListFragment = RecipeStepsListFragment.newInstance(recipe);
                recipeStepsListFragment.activateItemHighlighting();
                fragmentTransaction.replace(R.id.recipe_steps_list_container, recipeStepsListFragment);
                fragmentTransaction.commit();*/
                recipeStepsListFragment.activateItemHighlighting();


                // start with the first step at position 0
                //Step step = recipe.getSteps().get(0);
                // create a new ArrayList using the List of Steps
                ArrayList<Step> steps = new ArrayList<Step>(recipe.getSteps());

                // In two-pane mode, add initial RecipeStepDetailsFragment to the screen
                fragmentTransaction = fragmentManager.beginTransaction();
                // use newInstance method instead of constructor, passing in the list of steps and selected step index
                Fragment recipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex);
                fragmentTransaction.replace(R.id.recipe_step_details_container, recipeStepDetailsFragment);
                fragmentTransaction.commit();

                /*RecipeStepsListFragment newFragment = RecipeStepsListFragment.newInstance(recipe);
                newFragment.activateItemHighlighting();*/

            }
            else {
                Log.e(TAG, "recipe retrieved from intent was null");
            }
        }
        /*else {
            // create the recipe steps list in single-pane layout
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = RecipeStepsListFragment.newInstance(recipe);
            fragmentTransaction.replace(R.id.recipe_steps_list_container, fragment);
            fragmentTransaction.commit();
        }*/

    }

    // Define the behavior for onRecipeStepSelected
    @Override
    public void onRecipeStepSelected(int position) {
        // create a new ArrayList using the List of Steps
        ArrayList<Step> steps = new ArrayList<Step>(recipe.getSteps());
        stepIndex = position;

        // Handle the two-pane case and replace existing fragment right when a new step is selected from the master list
        if (isTwoPane) {
            // Create two-pane interaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            //RecipeStepDetailsFragment recipeStepDetailsFragment = new RecipeStepDetailsFragment();
            RecipeStepDetailsFragment recipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex);
            //recipeStepDetailsFragment.setSteps(steps);

            // Replace the old recipe step details fragment with a new one
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_details_container, recipeStepDetailsFragment)
                    .commit();

        } else {
            // Handle the single-pane phone case by passing the list of Steps and
            // index in Extras attached to an Intent

            // launch the recipe step details screen
            Intent launchStepDetailsActivity = new Intent(this, RecipeStepDetailsActivity.class);
            launchStepDetailsActivity.putExtra(EXTRA_STEPS, steps);
            launchStepDetailsActivity.putExtra(EXTRA_LIST_ITEM_INDEX, stepIndex);
            // display selected step details in new Activity only if single-pane
            startActivity(launchStepDetailsActivity);
        }
    }
}
