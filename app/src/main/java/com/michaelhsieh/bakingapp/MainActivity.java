package com.michaelhsieh.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Recipe>> call = service.getAllRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, "Server returned data", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Server returned data");
                    Log.d(TAG, "recipe data: " + response.body());
                }
                else {
                    //Toast.makeText(MainActivity.this, "Server returned an error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Server returned an error: " + response.errorBody());
                }
            }

            // Throwable t is the actual error object
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong. Please try again later!", Toast.LENGTH_SHORT).show();
                if (t instanceof IOException) {
                    //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "network failure occurred");
                }
                else {
                    //Toast.makeText(MainActivity.this, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "conversion issue");
                    Log.e(TAG, "conversion error is: " + t);
                }
            }
        });
    }
}
