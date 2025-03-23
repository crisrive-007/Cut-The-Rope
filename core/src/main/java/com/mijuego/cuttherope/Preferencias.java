/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.Serializable;

/**
 *
 * @author river
 */
public class Preferencias implements Serializable {

    private static final long serialVersionUID = 1L;

    private float volumen;
    private String idioma;

    public Preferencias() {
        this.volumen = 0.5f;
        this.idioma = "es";
    }

    public float getVolumen() {
        return volumen;
    }

    public void setVolumen(float volumen) {
        this.volumen = volumen;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
}
