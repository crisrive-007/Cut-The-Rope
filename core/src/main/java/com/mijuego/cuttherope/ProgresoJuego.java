/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author river
 */
public class ProgresoJuego implements Serializable {

    private static final long serialVersionUID = 1L;

    private int nivelActual;
    private Map<Integer, Integer> puntajesPorNivel;
    private Map<Integer, Boolean> nivelesCompletados;
    private int estrellasTotales;

    public ProgresoJuego() {
        this.nivelActual = 1;
        this.puntajesPorNivel = new HashMap<>();
        this.nivelesCompletados = new HashMap<>();
        this.estrellasTotales = 0;
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(int nivelActual) {
        this.nivelActual = nivelActual;
    }

    public int getPuntajeNivel(int nivel) {
        return puntajesPorNivel.getOrDefault(nivel, 0);
    }

    public void actualizarPuntajeNivel(int nivel, int puntaje) {
        int puntajeActual = puntajesPorNivel.getOrDefault(nivel, 0);
        if (puntaje > puntajeActual) {
            puntajesPorNivel.put(nivel, puntaje);
        }
    }

    public boolean isNivelCompletado(int nivel) {
        return nivelesCompletados.getOrDefault(nivel, false);
    }

    public void completarNivel(int nivel) {
        nivelesCompletados.put(nivel, true);
    }

    public int getEstrellasTotales() {
        return estrellasTotales;
    }

    public void agregarEstrellas(int estrellas) {
        this.estrellasTotales += estrellas;
    }

    public String mostrarNivelesCompletados() {
        System.out.println("Niveles completados:");
        String niveles = "";
        for (Map.Entry<Integer, Boolean> entry : nivelesCompletados.entrySet()) {
            if (entry.getValue()) {
                niveles = "Nivel " + entry.getKey() + " completado";
            }
        }
        return niveles;
    }
}
