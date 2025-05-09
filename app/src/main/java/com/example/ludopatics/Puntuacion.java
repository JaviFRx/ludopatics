package com.example.ludopatics;

public class Puntuacion {
    public String uid;
    public String nombre;
    public int puntuacion;

    public Puntuacion(){
        // Constructor vacío necesario para Moshi (y Firebase)
    }

    public Puntuacion(String uid, String nombre, int puntuacion) {
        this.uid = uid;
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }
}