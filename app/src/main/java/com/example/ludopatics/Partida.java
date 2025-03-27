package com.example.ludopatics;

public class Partida {
    private int idPartida;      // ID de la partida
    private int idJugador;      // ID del jugador
    private String fecha;       // Fecha de la partida
    private double saldoFinal;  // Saldo final después de la partida

    // Constructor para inicializar los campos
    public Partida(int idPartida, int idJugador, String fecha, double saldoFinal) {
        this.idPartida = idPartida;
        this.idJugador = idJugador;
        this.fecha = fecha;
        this.saldoFinal = saldoFinal;
    }

    // Métodos getter para acceder a los valores de los campos
    public int getIdPartida() {
        return idPartida;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public String getFecha() {
        return fecha;
    }

    public double getSaldoFinal() {
        return saldoFinal;
    }

    // Métodos setter si es necesario modificarlos
    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setSaldoFinal(double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}
