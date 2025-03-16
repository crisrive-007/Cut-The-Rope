/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.IOException;
import java.io.RandomAccessFile;
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

    //gets
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

    //sets
    public void setIdentificadorUnico(String identificadorUnico) {
        this.identificadorUnico = identificadorUnico;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

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

    public void guardarUsuario(RandomAccessFile raf) throws IOException {
        raf.writeBytes(identificadorUnico + "\n");
        raf.writeBytes(contraseña + "\n");
        raf.writeBytes(nombreCompleto + "\n");
        raf.writeLong(fechaRegistro.getTime());  // Guardar como long
        raf.writeBytes(ultimaSesion != null ? ultimaSesion.getTime() + "\n" : "null\n");
        raf.writeInt(progresoJuego);  // Guardar como int
        raf.writeInt(tiempoTotalJugado);  // Guardar como int
        raf.writeBytes(historialPartidas + "\n");
        raf.writeBytes(preferenciasJuego + "\n");
        raf.writeBytes(avatar + "\n");
        raf.writeInt(ranking);  // Guardar como int
        raf.writeBytes(amigos + "\n");
    }

    public static Usuarios cargarUsuario(RandomAccessFile raf) throws IOException {
        String identificadorUnico = raf.readLine();
        String contraseña = raf.readLine();
        String nombreCompleto = raf.readLine();
        Date fechaRegistro = new Date(raf.readLong());  // Leer como long
        Date ultimaSesion = null;
        String ultimaSesionStr = raf.readLine();
        if (!"null".equals(ultimaSesionStr)) {
            ultimaSesion = new Date(Long.parseLong(ultimaSesionStr));
        }
        int progresoJuego = raf.readInt();  // Leer como int
        int tiempoTotalJugado = raf.readInt();  // Leer como int
        String historialPartidas = raf.readLine();
        String preferenciasJuego = raf.readLine();
        String avatar = raf.readLine();
        int ranking = raf.readInt();  // Leer como int
        String amigos = raf.readLine();

        Usuarios usuario = new Usuarios(identificadorUnico, contraseña, nombreCompleto);
        usuario.fechaRegistro = fechaRegistro;
        usuario.ultimaSesion = ultimaSesion;
        usuario.progresoJuego = progresoJuego;
        usuario.tiempoTotalJugado = tiempoTotalJugado;
        usuario.historialPartidas = historialPartidas;
        usuario.preferenciasJuego = preferenciasJuego;
        usuario.avatar = avatar;
        usuario.ranking = ranking;
        usuario.amigos = amigos;

        return usuario;
    }

}
