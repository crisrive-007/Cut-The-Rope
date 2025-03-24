/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author river
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombreUsuario;
    private String contraseña; // Hash de la contraseña
    private String nombreCompleto;
    private long fechaRegistro;
    private long ultimaSesion;
    private ProgresoJuego progresoJuego;
    private long tiempoTotalJugado; // en milisegundos
    private List<Partida> historialPartidas;
    private Preferencias preferencias;
    private String rutaAvatar;
    private int puntuacionGeneral;
    private List<Amigo> amigos; // Lista de objetos Amigo en vez de String

    public Usuario(String nombreUsuario, String contraseña, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = System.currentTimeMillis();
        this.ultimaSesion = this.fechaRegistro;
        this.progresoJuego = new ProgresoJuego();
        this.tiempoTotalJugado = 0;
        this.historialPartidas = new ArrayList<>();
        this.preferencias = new Preferencias();
        this.rutaAvatar = "perfil-imagen.jpg";
        this.puntuacionGeneral = 0;
        this.amigos = new ArrayList<>(); // Inicializa lista de amigos
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public long getFechaRegistro() {
        return fechaRegistro;
    }

    public long getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(long ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public ProgresoJuego getProgresoJuego() {
        return progresoJuego;
    }

    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public void actualizarTiempoJugado(long duracionPartida) {
        this.tiempoTotalJugado += duracionPartida;
    }

    public List<Partida> getHistorialPartidas() {
        return historialPartidas;
    }

    public void agregarPartida(Partida partida) {
        this.historialPartidas.add(partida);
    }

    public Preferencias getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(Preferencias preferencias) {
        this.preferencias = preferencias;
    }

    public String getRutaAvatar() {
        return rutaAvatar;
    }

    public void setRutaAvatar(String rutaAvatar) {
        this.rutaAvatar = rutaAvatar;
    }

    public int getPuntuacionGeneral() {
        return puntuacionGeneral;
    }

    public void actualizarPuntaje(int puntos) {
        this.puntuacionGeneral += puntos;
    }

    // Métodos para acceder a la lista de amigos
    public List<Amigo> getAmigos() {
        return amigos;
    }

    // No se incluyen métodos para gestionar amigos ya que ahora
    // eso lo maneja la clase Control
    @Override
    public String toString() {
        int amigosAceptados = 0;
        for (Amigo amigo : amigos) {
            if (amigo.isAceptado()) {
                amigosAceptados++;
            }
        }

        return "Usuario: " + nombreUsuario
                + "\nNombre completo: " + nombreCompleto
                + "\nFecha de registro: " + new Date(fechaRegistro)
                + "\nÚltima sesión: " + new Date(ultimaSesion)
                + "\nNivel actual: " + progresoJuego.getNivelActual()
                + "\nTiempo total jugado: " + (tiempoTotalJugado / 3600000) + " horas"
                + "\nPuntuación general: " + puntuacionGeneral
                + "\nNúmero de amigos: " + amigosAceptados;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Usuario usuario = (Usuario) obj;
        return nombreUsuario != null && nombreUsuario.equals(usuario.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return nombreUsuario != null ? nombreUsuario.hashCode() : 0;
    }
}
