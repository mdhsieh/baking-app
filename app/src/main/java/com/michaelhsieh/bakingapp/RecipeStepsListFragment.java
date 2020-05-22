package com.michaelhsieh.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelhsieh.bakingapp.model.Ingredient;
import com.michaelhsieh.bakingapp.model.Recipe;
import com.michaelhsieh.bakingapp.model.Step;

import java.util.List;


/** Displays a selected recipe's steps in one list.
 * The user can view the recipe's ingredients and select one of the recipe steps.
 * This is the master list fragment in the master/detail flow of the tablet layout.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeStepsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepsListFragment extends Fragment implements RecipeStepsListAdapter.ItemClickListener {

    private static final String TAG = RecipeStepsListFragment.class.getSimpleName();

    /*// Step key when sending Intent
    private static final String EXTRA_STEP = "Step";*/

    // parameter argument with name that matches
    // the fragment initialization parameters
    private static final String ARG_RECIPE = "Recipe";

    // parameter of type Recipe
    private Recipe recipe;

    private RecyclerView recyclerView;
    private RecipeStepsListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // Define a new interface OnRecipeStepClickListener that triggers a callback in the host activity
    private OnRecipeStepClickListener callback;

    // OnRecipeStepClickListener interface, calls a method in the host activity named onRecipeStepSelected
    /* OnRecipeStepSelected will change the step in the host activity and change
    the step details fragment to match the selected step
    */
    public interface OnRecipeStepClickListener
    {
        void onRecipeStepSelected(int position);
    }

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
        args.putParcelable(ARG_RECIPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            callback = (OnRecipeStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRecipeStepClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_recipe_steps_list, container, false);

        TextView ingredientsDisplay = (TextView) rootView.findViewById(R.id.tv_recipe_ingredients);

        // set up the RecyclerView
        recyclerView = rootView.findViewById(R.id.rv_recipe_steps);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // add title to text
        ingredientsDisplay.append("Ingredients: " + "\n" + "\n");

        // String to store ingredient quantity, measure, and name in one line
        String ingredientInfo;

        if (recipe != null) {
            List<Ingredient> ingredients = recipe.getIngredients();

            // append each ingredient on a separate line
            for (Ingredient ingredient : ingredients) {
                ingredientInfo = ingredient.getQuantity() + " " + ingredient.getMeasure() +
                        " " + ingredient.getIngredient() + "\n";
                ingredientsDisplay.append(ingredientInfo);
            }

            List<Step> steps = recipe.getSteps();

            /* Use isAdded to avoid a null pointer exception when the fragment is
            detached from the activity. isAdded gives the same result as getActivity() == null.

            For example:
            When pressing the back button to return to a previous activity, the fragment is
            no longer attached to the activity, so getActivity() returns null.
            If the check isn't there the app will crash.
             */

            if (isAdded()) {
                // specify the adapter
                adapter = new RecipeStepsListAdapter(getContext(), steps);
                adapter.setClickListener(this);
                recyclerView.setAdapter(adapter);
            }
            else {
                Log.e(TAG, "the fragment is not currently added to its activity");
            }
        }

        return rootView;
    }

    // called when a step in the RecipeStepsListAdapter is clicked
    @Override
    public void onRecipeStepItemClick(View view, int position) {
        // get the Step that was clicked
        //Step step = adapter.getItem(position);
        // trigger the callback onRecipeStepSelected when an item is clicked
        callback.onRecipeStepSelected(position);
    }
}
