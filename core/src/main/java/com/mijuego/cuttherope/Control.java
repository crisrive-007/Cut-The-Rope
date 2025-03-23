/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author river
 */
public class Control {

    private static final String DIRECTORIO_BASE = "usuarios";
    private static final String ARCHIVO_USUARIOS = "usuarios.ser";
    private Map<String, Usuario> usuarios;

    public Control() {
        try {
            inicializarDirectorios();
            cargarUsuarios();
        } catch (Exception e) {
            System.err.println("Error al inicializar el sistema de usuarios: " + e.getMessage());
            e.printStackTrace();
            usuarios = new HashMap<>();
        }
    }

    private void inicializarDirectorios() {
        File dirBase = new File(DIRECTORIO_BASE);
        if (!dirBase.exists()) {
            dirBase.mkdir();
        }
    }

    private void cargarUsuarios() throws IOException, ClassNotFoundException {
        File archivoUsuarios = new File(DIRECTORIO_BASE + "/" + ARCHIVO_USUARIOS);
        if (archivoUsuarios.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoUsuarios))) {
                usuarios = (Map<String, Usuario>) ois.readObject();
            }
        } else {
            usuarios = new HashMap<>();
        }
    }

    private void guardarUsuarios() throws IOException {
        File archivoUsuarios = new File(DIRECTORIO_BASE + "/" + ARCHIVO_USUARIOS);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoUsuarios))) {
            oos.writeObject(usuarios);
        }
    }

    public boolean registrarUsuario(String nombreUsuario, String contraseña, String nombreCompleto) {
        if (usuarios.containsKey(nombreUsuario)) {
            System.out.println("El nombre de usuario ya existe.");
            return false;
        }

        try {
            String contraseñaHash = hashContraseña(contraseña);
            Usuario nuevoUsuario = new Usuario(nombreUsuario, contraseñaHash, nombreCompleto);

            // Crear directorio personal del usuario
            File dirUsuario = new File(DIRECTORIO_BASE + "/usuario_" + nombreUsuario);
            dirUsuario.mkdir();

            // Guardar usuario
            usuarios.put(nombreUsuario, nuevoUsuario);
            guardarUsuarios();
            guardarUsuarioIndividual(nuevoUsuario);

            System.out.println("Usuario " + nombreUsuario + " registrado exitosamente.");
            return true;
        } catch (Exception e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void guardarUsuarioIndividual(Usuario usuario) throws IOException {
        String dirUsuario = DIRECTORIO_BASE + "/usuario_" + usuario.getNombreUsuario();
        File archivoUsuario = new File(dirUsuario + "/datos.ser");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoUsuario))) {
            oos.writeObject(usuario);
        }

        // Guardar historial de partidas por separado para facilitar actualizaciones
        File archivoHistorial = new File(dirUsuario + "/historial.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoHistorial))) {
            oos.writeObject(usuario.getHistorialPartidas());
        }

        // Guardar preferencias por separado
        File archivoPreferencias = new File(dirUsuario + "/preferencias.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoPreferencias))) {
            oos.writeObject(usuario.getPreferencias());
        }
    }

    private String hashContraseña(String contraseña) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(contraseña.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public boolean iniciarSesion(String nombreUsuario, String contraseña) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            return false;
        }

        try {
            String contraseñaHash = hashContraseña(contraseña);
            if (usuario.getContraseña().equals(contraseñaHash)) {
                // Actualizar fecha de última sesión
                usuario.setUltimaSesion(System.currentTimeMillis());
                guardarUsuarios();
                guardarUsuarioIndividual(usuario);
                System.out.println("Inicio de sesión exitoso para " + nombreUsuario);
                return true;
            } else {
                System.out.println("Contraseña incorrecta.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarAvatar(String nombreUsuario, String rutaImagen) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            return false;
        }

        try {
            // Cargamos la imagen
            File imagenOrigen = new File(rutaImagen);
            if (!imagenOrigen.exists()) {
                return false;
            }

            BufferedImage imagen = ImageIO.read(imagenOrigen);
            if (imagen == null) {
                return false;
            }

            // Guardamos la imagen en el directorio del usuario
            String dirUsuario = DIRECTORIO_BASE + "/usuario_" + nombreUsuario;
            String extension = rutaImagen.substring(rutaImagen.lastIndexOf('.') + 1);
            File archivoDestino = new File(dirUsuario + "/avatar." + extension);

            ImageIO.write(imagen, extension, archivoDestino);

            // Actualizamos la ruta del avatar en el objeto usuario
            usuario.setRutaAvatar(archivoDestino.getPath());
            guardarUsuarios();
            guardarUsuarioIndividual(usuario);

            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar avatar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPreferencias(String nombreUsuario, Preferencias nuevasPreferencias) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            return false;
        }

        try {
            usuario.setPreferencias(nuevasPreferencias);
            guardarUsuarios();
            guardarUsuarioIndividual(usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar preferencias: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarPartida(String nombreUsuario, Partida partida) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            return false;
        }

        try {
            usuario.agregarPartida(partida);
            usuario.actualizarTiempoJugado(partida.getDuracion());

            // Actualizar progreso y ranking si es necesario
            if (partida.getNivelCompletado() > usuario.getProgresoJuego().getNivelActual()) {
                usuario.getProgresoJuego().setNivelActual(partida.getNivelCompletado());
            }
            usuario.actualizarPuntaje(partida.getPuntos());

            guardarUsuarios();
            guardarUsuarioIndividual(usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al registrar partida: " + e.getMessage());
            return false;
        }
    }

    public boolean agregarAmigo(String nombreUsuario, String nombreAmigo) {
        Usuario usuario = usuarios.get(nombreUsuario);
        Usuario amigo = usuarios.get(nombreAmigo);

        if (usuario == null || amigo == null) {
            return false;
        }

        try {
            usuario.agregarAmigo(nombreAmigo);
            guardarUsuarios();
            guardarUsuarioIndividual(usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar amigo: " + e.getMessage());
            return false;
        }
    }

    public List<String> obtenerRanking() {
        List<Map.Entry<String, Usuario>> listaUsuarios = new ArrayList<>(usuarios.entrySet());

        // Ordenar por puntuación
        listaUsuarios.sort((e1, e2)
                -> Integer.compare(e2.getValue().getPuntuacionGeneral(), e1.getValue().getPuntuacionGeneral()));

        List<String> ranking = new ArrayList<>();
        for (Map.Entry<String, Usuario> entry : listaUsuarios) {
            Usuario u = entry.getValue();
            ranking.add(u.getNombreUsuario() + " - " + u.getPuntuacionGeneral() + " puntos");
        }

        return ranking;
    }

    public Usuario obtenerUsuario(String nombreUsuario) {
        return usuarios.get(nombreUsuario);
    }

    public Usuario buscarUsuario(String criterioBusqueda, String tipoBusqueda) {
        if (criterioBusqueda == null || tipoBusqueda == null) {
            return null;
        }

        switch (tipoBusqueda.toLowerCase()) {
            case "usuario":
                return usuarios.get(criterioBusqueda);

            case "nombre":
                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getNombreCompleto().equalsIgnoreCase(criterioBusqueda)) {
                        return usuario;
                    }
                }
                break;

            case "fecha":
                try {
                    // Convertir fecha de formato dd/MM/yyyy a timestamp
                    String[] partes = criterioBusqueda.split("/");
                    if (partes.length != 3) {
                        return null;
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.set(Integer.parseInt(partes[2]), Integer.parseInt(partes[1]) - 1, Integer.parseInt(partes[0]), 0, 0, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    long fechaBusqueda = cal.getTimeInMillis();

                    // Buscar usuarios registrados en esa fecha (mismo día)
                    for (Usuario usuario : usuarios.values()) {
                        Calendar calUsuario = Calendar.getInstance();
                        calUsuario.setTimeInMillis(usuario.getFechaRegistro());

                        Calendar calBusqueda = Calendar.getInstance();
                        calBusqueda.setTimeInMillis(fechaBusqueda);

                        if (calUsuario.get(Calendar.YEAR) == calBusqueda.get(Calendar.YEAR)
                                && calUsuario.get(Calendar.MONTH) == calBusqueda.get(Calendar.MONTH)
                                && calUsuario.get(Calendar.DAY_OF_MONTH) == calBusqueda.get(Calendar.DAY_OF_MONTH)) {
                            return usuario;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al buscar por fecha: " + e.getMessage());
                }
                break;

            case "nivel":
                try {
                    int nivelBuscado = Integer.parseInt(criterioBusqueda);
                    for (Usuario usuario : usuarios.values()) {
                        if (usuario.getProgresoJuego().getNivelActual() >= nivelBuscado) {
                            return usuario;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("El nivel debe ser un número: " + e.getMessage());
                }
                break;

            case "puntos":
                try {
                    int puntosBuscados = Integer.parseInt(criterioBusqueda);
                    for (Usuario usuario : usuarios.values()) {
                        if (usuario.getPuntuacionGeneral() >= puntosBuscados) {
                            return usuario;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Los puntos deben ser un número: " + e.getMessage());
                }
                break;

            default:
                System.err.println("Tipo de búsqueda no válido. Use: usuario, nombre, fecha, nivel o puntos");
                break;
        }

        return null;
    }

    public List<Usuario> buscarUsuarios(String criterioBusqueda, String tipoBusqueda) {
        List<Usuario> resultados = new ArrayList<>();

        if (criterioBusqueda == null || tipoBusqueda == null) {
            return resultados;
        }

        switch (tipoBusqueda.toLowerCase()) {
            case "usuario":
                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getNombreUsuario().toLowerCase().contains(criterioBusqueda.toLowerCase())) {
                        resultados.add(usuario);
                    }
                }
                break;

            case "nombre":
                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getNombreCompleto().toLowerCase().contains(criterioBusqueda.toLowerCase())) {
                        resultados.add(usuario);
                    }
                }
                break;

            case "fecha":
                try {
                    // Convertir fecha de formato dd/MM/yyyy a timestamp
                    String[] partes = criterioBusqueda.split("/");
                    if (partes.length != 3) {
                        return resultados;
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.set(Integer.parseInt(partes[2]), Integer.parseInt(partes[1]) - 1, Integer.parseInt(partes[0]), 0, 0, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    long fechaBusqueda = cal.getTimeInMillis();

                    // Buscar usuarios registrados en esa fecha (mismo día)
                    for (Usuario usuario : usuarios.values()) {
                        Calendar calUsuario = Calendar.getInstance();
                        calUsuario.setTimeInMillis(usuario.getFechaRegistro());

                        Calendar calBusqueda = Calendar.getInstance();
                        calBusqueda.setTimeInMillis(fechaBusqueda);

                        if (calUsuario.get(Calendar.YEAR) == calBusqueda.get(Calendar.YEAR)
                                && calUsuario.get(Calendar.MONTH) == calBusqueda.get(Calendar.MONTH)
                                && calUsuario.get(Calendar.DAY_OF_MONTH) == calBusqueda.get(Calendar.DAY_OF_MONTH)) {
                            resultados.add(usuario);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al buscar por fecha: " + e.getMessage());
                }
                break;

            case "nivel":
                try {
                    int nivelBuscado = Integer.parseInt(criterioBusqueda);
                    for (Usuario usuario : usuarios.values()) {
                        if (usuario.getProgresoJuego().getNivelActual() >= nivelBuscado) {
                            resultados.add(usuario);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("El nivel debe ser un número: " + e.getMessage());
                }
                break;

            case "puntos":
                try {
                    int puntosBuscados = Integer.parseInt(criterioBusqueda);
                    for (Usuario usuario : usuarios.values()) {
                        if (usuario.getPuntuacionGeneral() >= puntosBuscados) {
                            resultados.add(usuario);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Los puntos deben ser un número: " + e.getMessage());
                }
                break;

            default:
                System.err.println("Tipo de búsqueda no válido. Use: usuario, nombre, fecha, nivel o puntos");
                break;
        }

        return resultados;
    }

    public Usuario cargarUsuarioDesdeArchivo(String nombreUsuario) {
        try {
            String dirUsuario = DIRECTORIO_BASE + "/usuario_" + nombreUsuario;
            File archivoUsuario = new File(dirUsuario + "/datos.ser");

            if (!archivoUsuario.exists()) {
                return null;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoUsuario))) {
                Usuario usuario = (Usuario) ois.readObject();
                return usuario;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar usuario desde archivo: " + e.getMessage());
            return null;
        }
    }
}
