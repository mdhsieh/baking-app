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
public class RecipeStepDetailsActivity extends AppCompatActivity {

    private static final String TAG = RecipeStepDetailsActivity.class.getSimpleName();

    // List of Steps key when retrieving Intent
    private static final String EXTRA_STEPS = "Steps";
    // index key when retrieving Intent
    private static final String EXTRA_LIST_ITEM_INDEX = "list_item_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        // get the ArrayList of Steps from the Intent that started this Activity
        Intent intent = getIntent();
        ArrayList<Step> steps = intent.getParcelableArrayListExtra(EXTRA_STEPS);
        if (steps != null) {
            // create a new ArrayList using the List of Steps
            //ArrayList<Step> steps = new ArrayList<Step>(stepsList);
            int stepIndex = intent.getIntExtra(EXTRA_LIST_ITEM_INDEX, 0);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = RecipeStepDetailsFragment.newInstance(steps, stepIndex);
            fragmentTransaction.replace(R.id.recipe_step_details_container, fragment);
            fragmentTransaction.commit();
        }
        else {
            Log.e(TAG, "steps retrieved from intent was null");
        }
    }
}
