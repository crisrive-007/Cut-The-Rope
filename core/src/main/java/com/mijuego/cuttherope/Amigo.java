/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author river
 */
public class Amigo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Usuario amigo;
    private boolean aceptado;
    private long fechaSolicitud;
    private long fechaAceptacion;

    // Constructor vacío necesario para serialización
    public Amigo() {
    }

    // Constructor con parámetros
    public Amigo(Usuario amigo) {
        this.amigo = amigo;
        this.aceptado = false;
        this.fechaSolicitud = System.currentTimeMillis();
    }

    // Getters y setters
    public Usuario getAmigo() {
        return amigo;
    }

    public void setAmigo(Usuario amigo) {
        this.amigo = amigo;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
        if (aceptado) {
            this.fechaAceptacion = Calendar.getInstance().getTimeInMillis();
        }
    }

    public Date getFechaSolicitud() {
        return new Date(fechaSolicitud);
    }

    public void setFechaSolicitud(long fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaAceptacion() {
        return aceptado ? new Date(fechaAceptacion) : null;
    }

    public void setFechaAceptacion(long fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    // Método para aceptar la solicitud de amistad
    public void aceptarSolicitud() {
        this.aceptado = true;
        this.fechaAceptacion = Calendar.getInstance().getTimeInMillis();
    }

    // Método para comparar objetos Amigo
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Amigo otroAmigo = (Amigo) obj;
        return amigo != null && otroAmigo.amigo != null
                && amigo.getNombreUsuario().equals(otroAmigo.amigo.getNombreUsuario());
    }

    @Override
    public int hashCode() {
        return amigo != null ? amigo.getNombreUsuario().hashCode() : 0;
    }

    @Override
    public String toString() {
        String nombreUsuario = (amigo != null) ? amigo.getNombreUsuario() : "null";
        return "Amigo{"
                + "usuario=" + nombreUsuario
                + ", aceptado=" + aceptado
                + ", fechaSolicitud=" + new Date(fechaSolicitud)
                + ", fechaAceptacion=" + (aceptado ? new Date(fechaAceptacion) : "pendiente")
                + '}';
    }
}
