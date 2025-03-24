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
import javax.swing.JOptionPane;

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
            JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe.");
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

            JOptionPane.showMessageDialog(null, "Usuario " + nombreUsuario + " registrado exitosamente.");
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

        File archivoAmigos = new File(dirUsuario + "/amigos.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoAmigos))) {
            oos.writeObject(usuario.getAmigos());
        }
        
        File archivoProgreso = new File(dirUsuario + "/progreso.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoProgreso))) {
            oos.writeObject(usuario.getProgresoJuego());
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
            JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            return false;
        }

        try {
            String contraseñaHash = hashContraseña(contraseña);
            if (usuario.getContraseña().equals(contraseñaHash)) {
                // Actualizar fecha de última sesión
                usuario.setUltimaSesion(System.currentTimeMillis());
                guardarUsuarios();
                guardarUsuarioIndividual(usuario);
                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso para " + nombreUsuario);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
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

    public boolean agregarAmigo(String nombreUsuario, String nombreAmigo, String idioma) {
        Usuario usuario = usuarios.get(nombreUsuario);
        Usuario amigo = usuarios.get(nombreAmigo);

        if (usuario == null || amigo == null) {
            return false;
        }

        try {
            enviarSolicitudAmistad(nombreUsuario, nombreAmigo, idioma);
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

    public boolean enviarSolicitudAmistad(String nombreUsuarioSolicitante, String nombreUsuarioDestino, String idioma) {
        Usuario solicitante = usuarios.get(nombreUsuarioSolicitante);
        Usuario destinatario = usuarios.get(nombreUsuarioDestino);

        boolean español = idioma.equals("es");
        String texto1 = null;
        String texto2 = null;
        String texto3 = null;

        if (español) {
            texto1 = "Este usuario no existe.";
            texto2 = "Este usuario ya es tu amigo.";
            texto3 = "No puedes agregarte a ti mismo.";
        } else {
            texto1 = "This user does not exist.";
            texto2 = "This user is already your friend.";
            texto3 = "You cannot add yourself.";
        }

        if (solicitante == null || destinatario == null) {
            JOptionPane.showMessageDialog(null, texto1);
            return false;
        }

        for (Amigo amigo : solicitante.getAmigos()) {
            if (amigo.getAmigo().getNombreUsuario().equals(nombreUsuarioDestino) && amigo.isAceptado()) {
                JOptionPane.showMessageDialog(null, texto2);
                return false;
            }
        }

        if (solicitante == destinatario) {
            JOptionPane.showMessageDialog(null, texto3);
            return false;
        }

        try {
            Amigo nuevoAmigo = new Amigo(solicitante);
            destinatario.getAmigos().add(nuevoAmigo);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar solicitud de amistad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// Método para aceptar una solicitud de amistad pendiente
    public boolean aceptarSolicitudAmistad(String nombreUsuarioAceptante, String nombreUsuarioSolicitante, String idioma) {
        Usuario aceptante = usuarios.get(nombreUsuarioAceptante);
        Usuario solicitante = usuarios.get(nombreUsuarioSolicitante);

        boolean español = idioma.equals("es");
        String texto1 = null;
        String texto2 = null;
        String texto3 = null;
        String texto4 = null;

        if (español) {
            texto1 = "Este usuario no existe.";
            texto2 = "No hay solicitud pendiente de ";
            texto3 = "Solicitud de amistad aceptada entre ";
            texto4 = " y ";
        } else {
            texto1 = "This user does not exist.";
            texto2 = "There is no pending request for ";
            texto3 = "Friend request accepted between ";
            texto4 = " and ";
        }

        if (aceptante == null || solicitante == null) {
            JOptionPane.showMessageDialog(null, texto1);
            return false;
        }

        // Buscar la solicitud pendiente
        boolean solicitudEncontrada = false;
        for (Amigo amigo : aceptante.getAmigos()) {
            if (amigo.getAmigo().getNombreUsuario().equals(nombreUsuarioSolicitante) && !amigo.isAceptado()) {
                amigo.aceptarSolicitud();
                solicitudEncontrada = true;
                break;
            }
        }

        if (!solicitudEncontrada) {
            JOptionPane.showMessageDialog(null, texto2 + nombreUsuarioSolicitante);
            return false;
        }

        // Crear relación bidireccional (si no existe ya)
        boolean relacionInversa = false;
        for (Amigo amigo : solicitante.getAmigos()) {
            if (amigo.getAmigo().getNombreUsuario().equals(nombreUsuarioAceptante)) {
                amigo.aceptarSolicitud();
                relacionInversa = true;
                break;
            }
        }

        if (!relacionInversa) {
            Amigo amigoInverso = new Amigo(aceptante);
            amigoInverso.aceptarSolicitud();
            solicitante.getAmigos().add(amigoInverso);
        }

        try {
            // Guardar los cambios
            guardarUsuarios();
            guardarUsuarioIndividual(aceptante);
            guardarUsuarioIndividual(solicitante);

            JOptionPane.showMessageDialog(null, texto3 + nombreUsuarioAceptante + texto4 + nombreUsuarioSolicitante);
            return true;
        } catch (Exception e) {
            System.err.println("Error al aceptar solicitud de amistad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// Método para rechazar una solicitud de amistad
    public boolean rechazarSolicitudAmistad(String nombreUsuarioRechazante, String nombreUsuarioSolicitante, String idioma) {
        Usuario rechazante = usuarios.get(nombreUsuarioRechazante);

        boolean español = idioma.equals("es");
        String texto1 = null;
        String texto2 = null;
        String texto3 = null;

        if (español) {
            texto1 = "Usuario no encontrado.";
            texto2 = "No hay solicitud pendiente de ";
            texto3 = "Solicitud de amistad rechazada de ";
        } else {
            texto1 = "User not found.";
            texto2 = "There is no pending request for ";
            texto3 = "Friend request rejected from ";
        }

        if (rechazante == null) {
            JOptionPane.showMessageDialog(null, texto1);
            return false;
        }

        boolean solicitudEliminada = false;
        List<Amigo> amigos = rechazante.getAmigos();
        for (int i = 0; i < amigos.size(); i++) {
            if (amigos.get(i).getAmigo().getNombreUsuario().equals(nombreUsuarioSolicitante)
                    && !amigos.get(i).isAceptado()) {
                amigos.remove(i);
                solicitudEliminada = true;
                break;
            }
        }

        if (!solicitudEliminada) {
            JOptionPane.showMessageDialog(null, texto2 + nombreUsuarioSolicitante);
            return false;
        }

        try {
            // Guardar los cambios
            guardarUsuarios();
            guardarUsuarioIndividual(rechazante);

            JOptionPane.showMessageDialog(null, texto3 + nombreUsuarioSolicitante);
            return true;
        } catch (Exception e) {
            System.err.println("Error al rechazar solicitud de amistad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// Método para eliminar un amigo
    public boolean eliminarAmigo(String nombreUsuario, String nombreAmigo, String idioma) {
        Usuario usuario = usuarios.get(nombreUsuario);
        Usuario amigo = usuarios.get(nombreAmigo);

        boolean español = idioma.equals("es");
        String texto1 = null;
        String texto2 = null;
        String texto3 = null;

        if (español) {
            texto1 = "Este usuario no existe.";
            texto2 = " no es amigo de ";
            texto3 = " ya no es tu amigo.";
        } else {
            texto1 = "This user does not exist.";
            texto2 = " its not a friend of ";
            texto3 = " is no longer your friend.";
        }

        if (usuario == null || amigo == null) {
            JOptionPane.showMessageDialog(null, texto1);
            return false;
        }

        boolean amigoEliminado = false;
        List<Amigo> listaAmigos = usuario.getAmigos();
        for (int i = 0; i < listaAmigos.size(); i++) {
            if (listaAmigos.get(i).getAmigo().getNombreUsuario().equals(nombreAmigo)) {
                listaAmigos.remove(i);
                amigoEliminado = true;
                break;
            }
        }

        List<Amigo> listaAmigosInversa = amigo.getAmigos();
        for (int i = 0; i < listaAmigosInversa.size(); i++) {
            if (listaAmigosInversa.get(i).getAmigo().getNombreUsuario().equals(nombreUsuario)) {
                listaAmigosInversa.remove(i);
                break;
            }
        }

        if (!amigoEliminado) {
            JOptionPane.showMessageDialog(null, nombreAmigo + texto2 + nombreUsuario);
            return false;
        }

        try {
            // Guardar los cambios
            guardarUsuarios();
            guardarUsuarioIndividual(usuario);
            guardarUsuarioIndividual(amigo);

            JOptionPane.showMessageDialog(null, nombreAmigo + texto3);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar amigo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// Método para obtener los amigos aceptados de un usuario
    public List<Usuario> obtenerAmigosAceptados(String nombreUsuario) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            return new ArrayList<>();
        }

        List<Usuario> amigosAceptados = new ArrayList<>();
        for (Amigo amigo : usuario.getAmigos()) {
            if (amigo.isAceptado()) {
                amigosAceptados.add(amigo.getAmigo());
            }
        }

        return amigosAceptados;
    }

// Método para obtener las solicitudes de amistad pendientes
    public List<Usuario> obtenerSolicitudesPendientes(String nombreUsuario) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            return new ArrayList<>();
        }

        List<Usuario> solicitudesPendientes = new ArrayList<>();
        for (Amigo amigo : usuario.getAmigos()) {
            if (!amigo.isAceptado()) {
                solicitudesPendientes.add(amigo.getAmigo());
            }
        }

        return solicitudesPendientes;
    }

// Método para verificar si dos usuarios son amigos
    public boolean sonAmigos(String nombreUsuario1, String nombreUsuario2) {
        Usuario usuario1 = usuarios.get(nombreUsuario1);
        if (usuario1 == null) {
            return false;
        }

        for (Amigo amigo : usuario1.getAmigos()) {
            if (amigo.getAmigo().getNombreUsuario().equals(nombreUsuario2) && amigo.isAceptado()) {
                return true;
            }
        }

        return false;
    }

    public boolean sumarPuntos(String nombreUsuario, int puntos) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            return false;
        }

        try {
            // Obtenemos la puntuación actual y sumamos los nuevos puntos
            int puntuacionActual = usuario.getPuntuacionGeneral();
            int nuevaPuntuacion = puntuacionActual + puntos;

            // Actualizamos la puntuación del usuario
            usuario.actualizarPuntaje(nuevaPuntuacion);

            // Guardamos los cambios
            guardarUsuarios();
            guardarUsuarioIndividual(usuario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al sumar puntos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int obtenerPuntos(String nombreUsuario) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            return -1;
        }
        return usuario.getPuntuacionGeneral();
    }
    
    public boolean actualizarProgresoNivel(String nombreUsuario, int nivel, int puntaje, int estrellas) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            return false;
        }

        ProgresoJuego progreso = usuario.getProgresoJuego();

        // Actualizar el progreso
        progreso.actualizarPuntajeNivel(nivel, puntaje);
        progreso.completarNivel(nivel);
        progreso.agregarEstrellas(estrellas);

        // Si se completó este nivel, desbloquear el siguiente
        if (nivel >= progreso.getNivelActual()) {
            progreso.setNivelActual(nivel + 1);
        }

        // Guardar los cambios
        return guardarProgresoJuego(nombreUsuario);
    }

    public boolean guardarProgresoJuego(String nombreUsuario) {
        try {
            // Verificar que el usuario existe
            Usuario usuario = usuarios.get(nombreUsuario);
            if (usuario == null) {
                System.err.println("Error: Usuario " + nombreUsuario + " no encontrado.");
                return false;
            }

            // Obtener la ruta del directorio del usuario
            String dirUsuario = DIRECTORIO_BASE + "/usuario_" + nombreUsuario;
            File dirUsuarioFile = new File(dirUsuario);

            // Verificar si el directorio existe, si no, crearlo
            if (!dirUsuarioFile.exists()) {
                if (!dirUsuarioFile.mkdirs()) {
                    System.err.println("Error: No se pudo crear el directorio para el usuario.");
                    return false;
                }
            }

            // Guardar el progreso del juego
            File archivoProgreso = new File(dirUsuario + "/progreso.ser");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoProgreso))) {
                ProgresoJuego progreso = usuario.getProgresoJuego();
                oos.writeObject(progreso);
                System.out.println("Progreso del juego guardado exitosamente para " + nombreUsuario);

                // También actualizar el usuario en el mapa general
                guardarUsuarios();
                guardarUsuarioIndividual(usuario);

                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el progreso del juego: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int obtenerNivelActual(String nombreUsuario) {
        Usuario usuario = usuarios.get(nombreUsuario);
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            return -1;
        }

        return usuario.getProgresoJuego().getNivelActual();
    }
}
