package com.example.ludopatics;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ludopatics.network.FirebaseApi;
import com.example.ludopatics.network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Puntuacion extends AppCompatActivity {

    private static final String TAG = "Puntuaciones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_top_ten);//HAY QUE CREAR UN LAYOUT

        // Obtener la instancia de Retrofit y la API
        String baseUrl = "https://console.firebase.google.com/u/0/project/ludopatics-bcee6/overview?hl=es-419 "; //HE PUESTO ESTO PARA QUE NO ME DE ERROR
        FirebaseApi api = RetrofitClient.getClient(baseUrl).create(FirebaseApi.class);

        // Llamar a getTopTen()
        Call<Map<String, Puntuacion>> call = api.getTopTen();

        call.enqueue(new Callback<Map<String, Puntuacion>>() {
            @Override
            public void onResponse(Call<Map<String, Puntuacion>> call, Response<Map<String, Puntuacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Map.Entry<String, Puntuacion> entry : response.body().entrySet()) {
                        Puntuacion p = entry.getValue();
                        Log.d(TAG, "Jugador: " + p.nombre + ", Puntos: " + p.puntuacion);
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Puntuacion>> call, Throwable t) {
                Log.e(TAG, "Fallo en la llamada", t);
            }
        });
    }
}

