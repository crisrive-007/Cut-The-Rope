/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author river
 */
public class Partida implements Serializable{
    private static final long serialVersionUID = 1L;

    private long fechaPartida;
    private int nivelJugado;
    private int nivelCompletado;
    private int puntos;
    private long duracion;
    private int estrellas;
    private List<String> logros;

    public Partida(int nivelJugado) {
        this.fechaPartida = Calendar.getInstance().getTimeInMillis();
        this.nivelJugado = nivelJugado;
        this.nivelCompletado = 0;
        this.puntos = 0;
        this.duracion = 0;
        this.estrellas = 0;
        this.logros = new ArrayList<>();
    }

    public void completarPartida(int puntos, long duracion, int estrellas) {
        this.nivelCompletado = this.nivelJugado;
        this.puntos = puntos;
        this.duracion = duracion;
        this.estrellas = estrellas;
    }

    public void agregarLogro(String logro) {
        this.logros.add(logro);
    }

    public long getFechaPartida() {
        return fechaPartida;
    }

    public int getNivelJugado() {
        return nivelJugado;
    }

    public int getNivelCompletado() {
        return nivelCompletado;
    }

    public int getPuntos() {
        return puntos;
    }

    public long getDuracion() {
        return duracion;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public List<String> getLogros() {
        return logros;
    }
}
