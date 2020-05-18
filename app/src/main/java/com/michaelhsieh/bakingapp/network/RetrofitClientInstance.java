package com.michaelhsieh.bakingapp.network;

import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import okhttp3.ConnectionSpec;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static final String TAG = RetrofitClientInstance.class.getSimpleName();

    // the entire URL of the online JSON data is
    // https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            /*
                Versions of Android below API 21 used TLS v1.0 and 1.1.
                Retrofit dropped support of those and supports TLS 1.2 by default,
                so we add the TLS value in if needed.

                Retrofit uses the OkHttp library for HTTP requests.
             */

            /* ConnectionSpec.MODERN_TLS is the default value */
            List tlsSpecs = Arrays.asList(ConnectionSpec.MODERN_TLS);

            /* provide backwards compatibility for API lower than Lollipop = API 21: */
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                tlsSpecs = Arrays.asList(ConnectionSpec.COMPATIBLE_TLS);
                Log.d(TAG, "detected device using API less than 21");
            }

            // interceptor to log HTTP request and response
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // the HTTP client used for requests
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(tlsSpecs)
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}
