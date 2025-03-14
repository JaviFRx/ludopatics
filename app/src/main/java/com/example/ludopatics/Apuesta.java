package com.example.ludopatics;

public class Apuesta {
    String tipo; // "color", "numero", "parImpar"
    String valor; // "rojo", "negro", "verde", "par", "impar" o número
    int monto; // Cantidad apostada

    public Apuesta(String tipo, String valor, int monto) {
        this.tipo = tipo;
        this.valor = valor;
        this.monto = monto;
    }

}
