package com.michaelhsieh.bakingapp;

import android.content.Context;
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

    // key to store highlighted position in bundle
    private static final String KEY_HIGHLIGHTED_POSITION = "highlighted_position";

    // position of the highlighted recipe step, default 0
    // should always match the selected step
    private int highlightedPosition;

    // parameter argument with name that matches
    // the fragment initialization parameters
    private static final String ARG_RECIPE = "Recipe";
    private static final String ARG_ALLOW_HIGHLIGHTING = "allow_highlighting";

    // parameter of type Recipe
    private Recipe recipe;
    // parameter of type boolean to highlight selected step in two-pane case
    private boolean allowHighlighting;

    private RecyclerView recyclerView;
    private RecipeStepsListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // Define a new interface OnRecipeStepClickListener that triggers a callback in the host activity
    private OnRecipeStepClickListener callback;

    // OnRecipeStepClickListener interface, calls a method in the host activity named onRecipeStepSelected
    /* OnRecipeStepSelected will change the step details fragment to match the selected step. */
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
     * @param recipe The Recipe to get ingredients and steps from.
     * @param allowHighlighting Whether a step should be highlighted when the user selects it.
     * @return A new instance of fragment RecipeDetailFragment.
     */
    public static RecipeStepsListFragment newInstance(Recipe recipe, boolean allowHighlighting) {
        RecipeStepsListFragment fragment = new RecipeStepsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        args.putBoolean(ARG_ALLOW_HIGHLIGHTING, allowHighlighting);
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
            allowHighlighting = getArguments().getBoolean(ARG_ALLOW_HIGHLIGHTING);
        }

        // get the last highlighted position before orientation change
        if (savedInstanceState != null) {
            highlightedPosition = savedInstanceState.getInt(KEY_HIGHLIGHTED_POSITION);
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

        // StringBuilder to store ingredients to send to recipe widget
        StringBuilder widgetIngredients = new StringBuilder();

        if (recipe != null) {
            List<Ingredient> ingredients = recipe.getIngredients();

            // append each ingredient on a separate line
            for (Ingredient ingredient : ingredients) {
                ingredientInfo = ingredient.getQuantity() + " " + ingredient.getMeasure() +
                        " " + ingredient.getIngredient() + "\n";
                ingredientsDisplay.append(ingredientInfo);

                // add ingredient info to widget ingredients text
                widgetIngredients.append(ingredientInfo);
            }

            // get the recipe name and ingredients and update the recipe widget
            //handleActionUpdateRecipeWidgets(getContext(), recipe.getName(), ingredientsDisplay.getText().toString());
            //handleActionUpdateRecipeWidgets(getContext(), recipe.getName(), widgetIngredients.toString());
            RecipeIngredientsDisplayService.handleActionUpdateRecipeWidgets(getContext(), recipe.getName(), widgetIngredients.toString());

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

                /* If fragment was constructed for two-pane layout, allow
                the step the user selects to be highlighted.
                 */
                if (allowHighlighting) {
                    activateItemHighlighting();
                }
            }
            else {
                Log.e(TAG, "the fragment is not currently added to its activity");
            }
        }

        return rootView;
    }

    /* In two-pane layout, the first item should be highlighted automatically when
    recipe steps list is initialized.
    When the screen is rotated, the item last highlighted before rotation should
    be highlighted instead. */
    private void activateItemHighlighting() {
        if (adapter != null) {
            // allow highlighting in two-pane layout
            adapter.setItemHighlightingActivated(true);
            // highlight pre-selected first item or
            // last selected item before orientation change
            adapter.setItemHighlightedPosition(highlightedPosition);
            // force onBindViewHolder again to update new selected item color
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "RecipeStepsListAdapter is null");
        }
    }

    // called when a step in the RecipeStepsListAdapter is clicked
    @Override
    public void onItemClick(View view, int position) {
        // trigger the callback onRecipeStepSelected when an item is clicked
        callback.onRecipeStepSelected(position);

        /* Change the position of the highlighted item to match the selected item.
        This indicates to the user which recipe step they are on in
        a two-pane layout. */
        if (adapter != null && allowHighlighting) {
            highlightedPosition = position;
            adapter.setItemHighlightedPosition(highlightedPosition);
            //adapter.setItemHighlightedPosition(position);
            // force onBindViewHolder again to update new selected item color
            adapter.notifyDataSetChanged();
        }
        else if (adapter != null) {
            // single-pane layout doesn't need highlighting
            //Log.v(TAG, "RecipeStepsListAdapter highlighting not allowed in single-pane layout");
        } else {
            Log.e(TAG, "RecipeStepsListAdapter is null");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_HIGHLIGHTED_POSITION, highlightedPosition);
    }

    /**
     * Update recipe widget ingredients list to match the selected Recipe
     */
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    /*public static void handleActionUpdateRecipeWidgets(Context context, String recipeName, String ingredients)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, RecipeWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);

        // Trigger the data update to handle the widget ingredient text and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text);

        // Update all widgets
        RecipeWidget.updateRecipeWidgets(context, appWidgetManager,
                recipeName, ingredients, appWidgetIds);

        Log.d(TAG, "updated recipe widget");
        //Log.d(TAG, "updated recipe widget with recipe: " + recipeName );
        //Log.d(TAG, "ingredients: " + ingredients);
    }*/
}
