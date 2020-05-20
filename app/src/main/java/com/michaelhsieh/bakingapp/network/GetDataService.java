package com.michaelhsieh.bakingapp.network;

import com.michaelhsieh.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface that defines the endpoint URL and GET HTTP request method.
 */

public interface GetDataService {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getAllRecipes();
}
