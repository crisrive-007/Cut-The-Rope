/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 *
 * @author river
 */
public class ControlUsuarios {

    private ArrayList<Usuarios> listaUsuarios;
    private String nombreArchivo;

    public ControlUsuarios(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        this.listaUsuarios = new ArrayList<>();
        cargarUsuarios();
    }

    public void agregarUsuario(Usuarios usuario) {
        listaUsuarios.add(usuario);
        salvarUsuarios();
    }

    public void salvarUsuarios() {
        try (RandomAccessFile raf = new RandomAccessFile(nombreArchivo, "rw")) {

            for (Usuarios usuario : listaUsuarios) {
                usuario.guardarUsuario(raf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarUsuarios() {
        try (RandomAccessFile raf = new RandomAccessFile(nombreArchivo, "r")) {

            while (raf.getFilePointer() < raf.length()) {
                Usuarios usuario = Usuarios.cargarUsuario(raf);
                listaUsuarios.add(usuario);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Usuarios> getListaUsuarios() {
        return listaUsuarios;
    }

    public Usuarios buscarUsuario(String identificadorUnico) {
        for (Usuarios usuario : listaUsuarios) {
            if (usuario.getIdentificadorUnico().equals(identificadorUnico)) {
                return usuario;
            }
        }
        return null;
    }
}
