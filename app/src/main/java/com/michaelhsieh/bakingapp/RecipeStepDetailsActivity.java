package com.michaelhsieh.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.michaelhsieh.bakingapp.model.Recipe;
import com.michaelhsieh.bakingapp.model.Step;

public class RecipeStepDetailsActivity extends AppCompatActivity {

    // Step key when retrieving Intent
    private static final String EXTRA_STEP = "Step";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        // get the Recipe from the Intent that started this Activity
        Intent intent = getIntent();
        Step step = intent.getParcelableExtra(EXTRA_STEP);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = RecipeStepDetailsFragment.newInstance(step);
        fragmentTransaction.replace(R.id.recipe_step_details_container, fragment);
        fragmentTransaction.commit();
    }
}
