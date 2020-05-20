package com.michaelhsieh.bakingapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Create the Retrofit object used to make requests.
 *
 */
public class RetrofitClientInstance {

    // the entire URL of the online JSON data is
    // https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            /*
                Android devices below API 21 used TLS v1.0 and 1.1.
                Retrofit dropped support of those versions and supports TLS 1.2 by default,
                so we force an old OkHttp version to be used with Retrofit in app build.gradle
                to support backwards compatibility.

                Retrofit uses the OkHttp library for HTTP requests.
             */

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
