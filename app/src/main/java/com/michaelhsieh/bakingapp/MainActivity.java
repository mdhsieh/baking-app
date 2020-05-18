package com.michaelhsieh.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.michaelhsieh.bakingapp.model.Recipe;
import com.michaelhsieh.bakingapp.network.GetDataService;
import com.michaelhsieh.bakingapp.network.RetrofitClientInstance;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Create handle for the RetrofitInstance interface */
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Recipe>> call = service.getAllRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    //Log.d(TAG, "recipe data: " + response.body());
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
                Toast.makeText(MainActivity.this, "Something went wrong. Please try again later!", Toast.LENGTH_SHORT).show();
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
        recyclerView = findViewById(R.id.rv_recipes);
        adapter = new RecipeAdapter(this, recipeList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
