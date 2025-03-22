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
public class Preferencias implements Serializable {

    private static final long serialVersionUID = 1L;

    private int volumenMusica;
    private int volumenEfectos;
    private String idioma;

    public Preferencias() {
        this.volumenMusica = 80;
        this.volumenEfectos = 100;
        this.idioma = "es";
        /*this.tutorialActivado = true;
        this.notificacionesActivadas = true;
        //this.controlesPersonalizados = new HashMap<>();
        this.modoOscuro = false;

        // Configuración de controles por defecto
        controlesPersonalizados.put("ARRIBA", "W");
        controlesPersonalizados.put("ABAJO", "S");
        controlesPersonalizados.put("IZQUIERDA", "A");
        controlesPersonalizados.put("DERECHA", "D");
        controlesPersonalizados.put("SALTAR", "ESPACIO");
        controlesPersonalizados.put("ACCION", "E");*/
    }

    public int getVolumenMusica() {
        return volumenMusica;
    }

    public void setVolumenMusica(int volumenMusica) {
        this.volumenMusica = Math.max(0, Math.min(100, volumenMusica));
    }

    public int getVolumenEfectos() {
        return volumenEfectos;
    }

    public void setVolumenEfectos(int volumenEfectos) {
        this.volumenEfectos = Math.max(0, Math.min(100, volumenEfectos));
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    /*public boolean isTutorialActivado() {
        return tutorialActivado;
    }

    public void setTutorialActivado(boolean tutorialActivado) {
        this.tutorialActivado = tutorialActivado;
    }

    public boolean isNotificacionesActivadas() {
        return notificacionesActivadas;
    }

    public void setNotificacionesActivadas(boolean notificacionesActivadas) {
        this.notificacionesActivadas = notificacionesActivadas;
    }

    public Map<String, String> getControlesPersonalizados() {
        return controlesPersonalizados;
    }

    public void setControl(String accion, String tecla) {
        controlesPersonalizados.put(accion, tecla);
    }

    public boolean isModoOscuro() {
        return modoOscuro;
    }

    public void setModoOscuro(boolean modoOscuro) {
        this.modoOscuro = modoOscuro;
    }*/
}
