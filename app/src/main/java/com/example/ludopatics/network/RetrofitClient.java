package com.example.ludopatics.network;

import com.squareup.moshi.Moshi;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://console.firebase.google.com/u/0/project/ludopatics-bcee6/overview?hl=es-419 ";
    private static Retrofit retrofit = null;

    public static FirebaseApi getApi() {
        if (retrofit == null) {
            Moshi moshi = new Moshi.Builder().build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build();
        }
        return retrofit.create(FirebaseApi.class);
    }


    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            Moshi moshi = new Moshi.Builder().build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build();
        }
        return retrofit;
    }
}