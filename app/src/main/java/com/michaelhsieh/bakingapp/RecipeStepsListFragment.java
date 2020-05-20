package com.michaelhsieh.bakingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelhsieh.bakingapp.model.Ingredient;
import com.michaelhsieh.bakingapp.model.Recipe;
import com.michaelhsieh.bakingapp.model.Step;

import java.util.List;


/** This fragment displays a selected recipe in more detail.
 * The user can view the recipe's ingredients and select one of the recipe steps.
 * This is the master list fragment in the master-detail flow of the tablet layout.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeStepsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepsListFragment extends Fragment {

    private static final String TAG = RecipeStepsListFragment.class.getSimpleName();

    // Recipe key when retrieving Intent from Activity
    private static final String EXTRA_RECIPE = "Recipe";

    private Recipe recipe;

    public RecipeStepsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 The Recipe to get ingredients and steps from.
     * @return A new instance of fragment RecipeDetailFragment.
     */
    public static RecipeStepsListFragment newInstance(Recipe param1) {
        RecipeStepsListFragment fragment = new RecipeStepsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_RECIPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(EXTRA_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_recipe_steps_list, container, false);

        TextView ingredientsDisplay = (TextView) rootView.findViewById(R.id.tv_recipe_ingredients);

        // add title to text
        ingredientsDisplay.append("Ingredients: " + "\n" + "\n");

        // String to store ingredient quantity, measure, and name in one line
        String ingredientInfo;

        String shortDescription;

        if (recipe != null) {
            List<Ingredient> ingredients = recipe.getIngredients();
            List<Step> steps = recipe.getSteps();
            // append each ingredient on a separate line
            for (Ingredient ingredient : ingredients) {
                ingredientInfo = ingredient.getQuantity() + " " + ingredient.getMeasure() +
                        " " + ingredient.getIngredient() + "\n";
                ingredientsDisplay.append(ingredientInfo);
                //Log.d(TAG, ingredientInfo);
            }

            for (Step step : steps) {
                shortDescription = step.getShortDescription();
                Log.d(TAG, shortDescription);
            }
        }

        return rootView;
    }
}
