/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;
import java.io.*;
import java.util.*;

public class ControlUsuarios implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient RandomAccessFile archivoUsuarios;
    private ArrayList<Usuarios> listaUsuarios;
    private String nombreArchivo;

    public ControlUsuarios(String nombreArchivo) throws IOException {
        this.nombreArchivo = nombreArchivo;
        this.listaUsuarios = new ArrayList<>();
        
        File folder = new File("usuarios");
        folder.mkdir();
        archivoUsuarios = new RandomAccessFile("usuarios/usuarios.usr", "rw");
        cargarUsuarios();
        deserializarUsuarios();
    }

    public void agregarUsuario(Usuarios usuario) throws IOException {
        archivoUsuarios.seek(archivoUsuarios.length());
        archivoUsuarios.writeUTF(usuario.getIdentificadorUnico());
        archivoUsuarios.writeUTF(usuario.getContraseña());
        archivoUsuarios.writeUTF(usuario.getNombreCompleto());
        archivoUsuarios.writeLong(usuario.getFechaRegistro().getTime());
        archivoUsuarios.writeLong(usuario.getUltimaSesion() != null ? usuario.getUltimaSesion().getTime() : 0);
        archivoUsuarios.writeInt(usuario.getProgresoJuego());
        archivoUsuarios.writeInt(usuario.getTiempoTotalJugado());
        archivoUsuarios.writeUTF(usuario.getHistorialPartidas());
        archivoUsuarios.writeUTF(usuario.getPreferenciasJuego());
        archivoUsuarios.writeUTF(usuario.getAvatar());
        archivoUsuarios.writeInt(usuario.getRanking());
        archivoUsuarios.writeUTF(usuario.getAmigos());
        listaUsuarios.add(usuario);
        serializarUsuarios();
    }

    private void cargarUsuarios() throws IOException {
        archivoUsuarios.seek(0);
        while (archivoUsuarios.getFilePointer() < archivoUsuarios.length()) {
            String identificadorUnico = archivoUsuarios.readUTF();
            String contraseña = archivoUsuarios.readUTF();
            String nombreCompleto = archivoUsuarios.readUTF();
            Date fechaRegistro = new Date(archivoUsuarios.readLong());
            long ultimaSesionLong = archivoUsuarios.readLong();
            Date ultimaSesion = (ultimaSesionLong == 0) ? null : new Date(ultimaSesionLong);
            int progresoJuego = archivoUsuarios.readInt();
            int tiempoTotalJugado = archivoUsuarios.readInt();
            String historialPartidas = archivoUsuarios.readUTF();
            String preferenciasJuego = archivoUsuarios.readUTF();
            String avatar = archivoUsuarios.readUTF();
            int ranking = archivoUsuarios.readInt();
            String amigos = archivoUsuarios.readUTF();

            Usuarios usuario = new Usuarios(identificadorUnico, contraseña, nombreCompleto);
            usuario.setUltimaSesion(ultimaSesion);
            usuario.setProgresoJuego(progresoJuego);
            usuario.setHistorialPartidas(historialPartidas);
            usuario.setPreferenciasJuego(preferenciasJuego);
            usuario.setAvatar(avatar);
            usuario.setAmigos(amigos);
            listaUsuarios.add(usuario);
        }
    }

    public Usuarios buscarUsuario(String identificadorUnico) {
        for (Usuarios usuario : listaUsuarios) {
            if (usuario.getIdentificadorUnico().equals(identificadorUnico)) {
                return usuario;
            }
        }
        return null;
    }

    public void actualizarUltimaSesion(String identificadorUnico) throws IOException {
        for (Usuarios usuario : listaUsuarios) {
            if (usuario.getIdentificadorUnico().equals(identificadorUnico)) {
                usuario.setUltimaSesion(new Date());
                salvarUsuarios();
                serializarUsuarios();
                break;
            }
        }
    }

    public void salvarUsuarios() throws IOException {
        archivoUsuarios.setLength(0);
        for (Usuarios usuario : listaUsuarios) {
            agregarUsuario(usuario);
        }
        serializarUsuarios();
    }

    // Métodos para serializar y deserializar usuarios
    private void serializarUsuarios() {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("usuarios/usuarios.dat"))) {
            salida.writeObject(listaUsuarios);
        } catch (IOException e) {
            System.out.println("Error al serializar usuarios: " + e.getMessage());
        }
    }

    private void deserializarUsuarios() {
        File archivo = new File("usuarios/usuarios.dat");
        if (archivo.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
                listaUsuarios = (ArrayList<Usuarios>) entrada.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error al deserializar usuarios: " + e.getMessage());
            }
        }
    }
}
