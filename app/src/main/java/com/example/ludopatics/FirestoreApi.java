package com.example.ludopatics;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FirestoreApi {

    // Obtener el valor del bote
    @GET("projects/ludopatics-bcee6/databases/(default)/documents/bote/valor")
    Call<JsonObject> getValorBote(@Header("Authorization") String bearerToken);

    // Obtener las puntuaciones desde Firestore
    @GET("projects/ludopatics-bcee6/databases/(default)/documents/puntuaciones")
    Call<JsonObject> getPuntuaciones(@Header("Authorization") String bearerToken);

    // Enviar nuevo valor al bote
    @POST("projects/ludopatics-bcee6/databases/(default)/documents/bote/valor")
    Call<JsonObject> postValorBote(
            @Header("Authorization") String bearerToken,
            @Body JsonObject body
    );

    // Enviar puntuación a Firestore
    @POST("projects/ludopatics-bcee6/databases/(default)/documents/puntuaciones")
    Call<JsonObject> postPuntuacion(
            @Header("Authorization") String bearerToken,
            @Body JsonObject body
    );
}
