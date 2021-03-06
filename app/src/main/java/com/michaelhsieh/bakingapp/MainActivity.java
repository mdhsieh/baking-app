package com.michaelhsieh.bakingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.michaelhsieh.bakingapp.IdlingResource.SimpleIdlingResource;
import com.michaelhsieh.bakingapp.model.Recipe;
import com.michaelhsieh.bakingapp.network.GetDataService;
import com.michaelhsieh.bakingapp.network.RetrofitClientInstance;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource idlingResource;

    // Recipe key when sending Intent
    private static final String EXTRA_RECIPE = "Recipe";

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;

    private ProgressBar loadingIndicator;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource()
    {
        if (idlingResource == null)
        {
            idlingResource = new SimpleIdlingResource();
        }

        return idlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the IdlingResource instance
        getIdlingResource();

        // show that the recipes are loading
        loadingIndicator = findViewById(R.id.pb_loading);
        loadingIndicator.setVisibility(View.VISIBLE);

        // create a GetDataService object
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Recipe>> call = service.getAllRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                // finished loading
                loadingIndicator.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    generateRecipeList(response.body());
                }
                else {
                    Log.e(TAG, "server returned an error: " + response.errorBody());
                }
            }

            // Throwable t is the actual error object
            // Check whether the error is because of network failure or JSON to model (Java classes) conversion
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // finished loading
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, R.string.loading_error, Toast.LENGTH_LONG).show();
                if (t instanceof IOException) {
                    // A network failure. Inform the user and possibly retry.
                    Log.e(TAG, "network failure occurred");
                }
                else {
                    // conversion issue from JSON to model
                    Log.e(TAG, "conversion error is: " + t);
                }
            }
        });
    }

    /* Method to generate List of Recipes using RecyclerView with custom adapter */
    private void generateRecipeList(List<Recipe> recipeList) {
        // set up the RecyclerView
        recyclerView = findViewById(R.id.rv_recipes);
        adapter = new RecipeAdapter(this, recipeList);
        adapter.setClickListener(this);
        // smaller devices have one column and tablets have two columns of Recipes
        final int columns = getResources().getInteger(R.integer.recipe_columns);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, columns, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    // called when a recipe in the RecipeAdapter is clicked
    @Override
    public void onItemClick(View view, int position) {
        // get the recipe that was clicked
        Recipe recipe = adapter.getItem(position);
        // launch the recipe details screen
        Intent launchDetailActivity = new Intent(this, DetailActivity.class);
        launchDetailActivity.putExtra(EXTRA_RECIPE, recipe);
        startActivity(launchDetailActivity);
    }
}
