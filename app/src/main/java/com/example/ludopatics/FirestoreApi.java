package com.example.ludopatics;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FirestoreApi {

    @GET("projects/ludopatics-bcee6/databases/(default)/documents/puntuaciones")
    Call<JsonObject> getPuntuaciones(@Header("Authorization") String bearerToken);

    @POST("projects/ludopatics-bcee6/databases/(default)/documents/puntuaciones")
    Call<JsonObject> postPuntuacion(
            @Header("Authorization") String bearerToken,
            @Body JsonObject body
    );
}
