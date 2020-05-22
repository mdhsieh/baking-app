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

/** This Activity uses a master/detail flow to change its display depending on device screen size.
 *
 * On smaller devices, this will display the recipe steps list only.
 * On larger devices like tablets, this will display a two-pane layout. That is,
 * The recipe steps list will be displayed together with the selected step details.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    // Recipe key when retrieving Intent
    private static final String EXTRA_RECIPE = "Recipe";

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get the Recipe from the Intent that started this Activity
        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra(EXTRA_RECIPE);

        // create the recipe steps list
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = RecipeStepsListFragment.newInstance(recipe);
        fragmentTransaction.replace(R.id.recipe_steps_list_container, fragment);
        fragmentTransaction.commit();

        // determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.two_pane_linear_layout) != null) {
            // this LinearLayout will only initially exist in the two-pane tablet case
            isTwoPane = true;

            if (recipe != null) {
                // start with the first step
                Step step = recipe.getSteps().get(0);

                // In two-pane mode, add initial RecipeStepDetailsFragment to the screen
                fragmentTransaction = fragmentManager.beginTransaction();
                Fragment recipeStepDetailsFragment = RecipeStepDetailsFragment.newInstance(step);
                fragmentTransaction.replace(R.id.recipe_step_details_container, recipeStepDetailsFragment);
                fragmentTransaction.commit();
            }
            else {
                Log.e(TAG, "recipe retrieved from intent was null");
            }
        }

    }
}
