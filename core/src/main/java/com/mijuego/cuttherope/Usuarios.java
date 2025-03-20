/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;
import java.util.Date;

/**
 *
 * @author river
 */
public class Usuarios {

    private String identificadorUnico;
    private String contraseña;
    private String nombreCompleto;
    private Date fechaRegistro;
    private Date ultimaSesion;
    private int progresoJuego;
    private int tiempoTotalJugado;
    private String historialPartidas;
    private String preferenciasJuego;
    private String avatar;
    private int ranking;
    private String amigos;

    public Usuarios(String identificadorUnico, String contraseña, String nombreCompleto) {
        this.identificadorUnico = identificadorUnico;
        this.contraseña = contraseña;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = new Date();
        this.ultimaSesion = null;
        this.progresoJuego = 0;
        this.tiempoTotalJugado = 0;
        this.historialPartidas = "";
        this.preferenciasJuego = "";
        this.avatar = "";
        this.ranking = 0;
        this.amigos = "";
    }

    // Getters
    public String getIdentificadorUnico() {
        return identificadorUnico;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public Date getUltimaSesion() {
        return ultimaSesion;
    }

    public int getProgresoJuego() {
        return progresoJuego;
    }

    public int getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public String getHistorialPartidas() {
        return historialPartidas;
    }

    public String getPreferenciasJuego() {
        return preferenciasJuego;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getRanking() {
        return ranking;
    }

    public String getAmigos() {
        return amigos;
    }

    // Setters
    public void setUltimaSesion(Date ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public void setProgresoJuego(int progresoJuego) {
        this.progresoJuego = progresoJuego;
    }

    public void setHistorialPartidas(String historialPartidas) {
        this.historialPartidas = historialPartidas;
    }

    public void setPreferenciasJuego(String preferenciasJuego) {
        this.preferenciasJuego = preferenciasJuego;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setAmigos(String amigos) {
        this.amigos = amigos;
    }
}
