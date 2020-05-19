package com.michaelhsieh.bakingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.michaelhsieh.bakingapp.model.Ingredient;
import com.michaelhsieh.bakingapp.model.Recipe;

import java.util.List;

// This will display either the recipe details fragment or
// the recipe details fragment with selected step details fragment
public class DetailActivity extends AppCompatActivity {

    public static final String TAG = DetailActivity.class.getSimpleName();

    // Recipe key when retrieving Intent
    private static final String EXTRA_RECIPE = "Recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get the Recipe from the Intent that started this Activity
        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra(EXTRA_RECIPE);

        if (recipe != null) {
            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                Log.d(TAG, ingredient.getQuantity() + " " + ingredient.getMeasure() +
                        " " + ingredient.getIngredient());
            }
        }
    }
}
