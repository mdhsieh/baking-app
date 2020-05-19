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

import java.util.List;


/** This fragment displays a selected recipe in more detail.
 * The user can view the recipe's ingredients and select one of the recipe steps.
 * This is the master list fragment in the master-detail flow of the tablet layout.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {

    private static final String TAG = RecipeDetailsFragment.class.getSimpleName();

    // Recipe key when retrieving Intent from Activity
    private static final String EXTRA_RECIPE = "Recipe";

    private Recipe recipe;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RecipeDetailFragment.
     */
    public static RecipeDetailsFragment newInstance(Recipe param1) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
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
        View rootView =  inflater.inflate(R.layout.fragment_recipe_details, container, false);

        TextView ingredientsDisplay = (TextView) rootView.findViewById(R.id.tv_recipe_ingredients);

        ingredientsDisplay.append("Ingredients: " + "\n" + "\n");

        String recipeLine;

        if (recipe != null) {
            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                recipeLine = ingredient.getQuantity() + " " + ingredient.getMeasure() +
                        " " + ingredient.getIngredient() + "\n";
                ingredientsDisplay.append(recipeLine);
                Log.d(TAG, recipeLine);
            }
        }

        return rootView;
    }
}
