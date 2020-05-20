package com.michaelhsieh.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.michaelhsieh.bakingapp.model.Recipe;

/** This will display either the recipe steps master list fragment or
 * the recipe steps master list fragment with selected step details fragment
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    // Recipe key when retrieving Intent
    private static final String EXTRA_RECIPE = "Recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get the Recipe from the Intent that started this Activity
        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra(EXTRA_RECIPE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = RecipeStepsListFragment.newInstance(recipe);
        fragmentTransaction.replace(R.id.recipe_details_fragment, fragment);
        fragmentTransaction.commit();
    }
}
