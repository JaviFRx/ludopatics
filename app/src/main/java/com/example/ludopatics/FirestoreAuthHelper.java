package com.example.ludopatics;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import java.time.Instant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirestoreAuthHelper {

    // 🔹 GET → Leer puntuaciones desde Firestore
    public static void obtenerDatosFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                String token = getTokenResult.getToken();
                String authHeader = "Bearer " + token;

                RetrofitClient.getInstance().getPuntuaciones(authHeader)
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    JsonObject data = response.body();
                                    Log.d("FirestoreREST", "Datos recibidos: " + data.toString());
                                } else {
                                    Log.e("FirestoreREST", "Error de Firestore: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("FirestoreREST", "Fallo: " + t.getMessage());
                            }
                        });
            });
        } else {
            Log.e("FirestoreREST", "Usuario no autenticado");
        }
    }

    // 🔹 POST → Enviar puntuación al backend de Firestore
    public static void enviarPuntuacionFirestore(String nombre, int puntuacion, String uid) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                String token = getTokenResult.getToken();
                String authHeader = "Bearer " + token;

                JsonObject fields = new JsonObject();
                fields.add("nombre", crearCampoTexto(nombre));
                fields.add("puntuacion", crearCampoEntero(puntuacion));
                fields.add("uid", crearCampoTexto(uid));
                fields.add("timestamp", crearCampoTimestamp(Instant.now().toString()));

                JsonObject body = new JsonObject();
                body.add("fields", fields);

                RetrofitClient.getInstance().postPuntuacion(authHeader, body)
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    Log.d("POST_FIRESTORE", "¡Puntuación enviada correctamente!");
                                } else {
                                    Log.e("POST_FIRESTORE", "Error al enviar: " + response.code() + " - " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("POST_FIRESTORE", "Fallo: " + t.getMessage());
                            }
                        });
            });
        } else {
            Log.e("POST_FIRESTORE", "Usuario no autenticado");
        }
    }

    // 🔹 GET → Leer el valor del bote desde Firestore
    public static void obtenerValorBote(ValorBoteCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                String token = getTokenResult.getToken();
                String authHeader = "Bearer " + token;

                // Realizamos la llamada Retrofit
                RetrofitClient.getInstance().getValorBote(authHeader)
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    JsonObject data = response.body();
                                    if (data != null && data.has("fields")) {
                                        JsonObject fields = data.getAsJsonObject("fields");

                                        if (fields.has("bote")) {
                                            JsonObject boteObject = fields.getAsJsonObject("bote");
                                            if (boteObject.has("integerValue")) {
                                                int valor = boteObject.get("integerValue").getAsInt();
                                                callback.onValorObtenido(valor);
                                            } else {
                                                Log.e("GET_BOTE", "'bote' no contiene 'integerValue'");
                                                callback.onValorObtenido(0);
                                            }
                                        } else {
                                            Log.e("GET_BOTE", "'fields' no contiene 'bote'");
                                            callback.onValorObtenido(0);
                                        }
                                    } else {
                                        Log.e("GET_BOTE", "Respuesta sin campo 'fields'");
                                        callback.onValorObtenido(0);
                                    }
                                } else {
                                    Log.e("GET_BOTE", "Error HTTP: " + response.code());
                                    callback.onValorObtenido(0);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("GET_BOTE", "Error en la solicitud: " + t.getMessage());
                                callback.onError(new Exception("Error al obtener el valor del bote: " + t.getMessage()));
                            }
                        });
            }).addOnFailureListener(e -> {
                Log.e("GET_BOTE", "Error al obtener el token: " + e.getMessage());
                callback.onError(new Exception("Error de autenticación: " + e.getMessage()));
            });
        } else {
            Log.e("GET_BOTE", "Usuario no autenticado");
            callback.onError(new Exception("Usuario no autenticado"));
        }
    }




    // 🔹 POST → Enviar valor del bote a Firestore
    public static void enviarValorBote(int valorBote) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                String token = getTokenResult.getToken();
                String authHeader = "Bearer " + token;

                JsonObject fields = new JsonObject();
                fields.add("valor", crearCampoEntero(valorBote));
                fields.add("timestamp", crearCampoTimestamp(Instant.now().toString()));

                JsonObject body = new JsonObject();
                body.add("fields", fields);

                RetrofitClient.getInstance().postValorBote(authHeader, body)
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    Log.d("POST_BOTE", "Valor del bote enviado correctamente");
                                } else {
                                    Log.e("POST_BOTE", "Error al enviar bote: " + response.code() + " - " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("POST_BOTE", "Fallo al enviar: " + t.getMessage());
                            }
                        });
            });
        } else {
            Log.e("POST_BOTE", "Usuario no autenticado");
        }
    }

    // 🔧 Métodos auxiliares para construir los campos del JSON
    private static JsonObject crearCampoTexto(String valor) {
        JsonObject campo = new JsonObject();
        campo.addProperty("stringValue", valor);
        return campo;
    }

    private static JsonObject crearCampoEntero(int valor) {
        JsonObject campo = new JsonObject();
        campo.addProperty("integerValue", String.valueOf(valor));
        return campo;
    }

    private static JsonObject crearCampoTimestamp(String isoDateTime) {
        JsonObject campo = new JsonObject();
        campo.addProperty("timestampValue", isoDateTime);
        return campo;
    }
}
