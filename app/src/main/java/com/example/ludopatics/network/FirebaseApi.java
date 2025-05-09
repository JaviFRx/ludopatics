package com.example.ludopatics.network;

import com.example.ludopatics.Puntuacion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

import java.util.Map;

public interface FirebaseApi {

    @GET("topten.json")
    Call<Map<String, Puntuacion>> getTopTen();

    @PUT("topten.json")
    Call<Void> updateTopTen(@Body Map<String, Puntuacion> puntuaciones);
}
