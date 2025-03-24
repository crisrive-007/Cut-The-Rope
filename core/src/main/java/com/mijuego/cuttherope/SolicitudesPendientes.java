/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author river
 */
public class SolicitudesPendientes extends JFrame {

    private JList<String> listaSolicitudes;
    private DefaultListModel<String> modeloLista;
    private Usuario usuario;
    private Amigos amigos;
    private String idioma;
    private boolean español;

    public SolicitudesPendientes(Usuario usuario, Amigos amigos, String idioma) {
        this.usuario = usuario;
        this.amigos = amigos;
        this.idioma = idioma;
        this.español = idioma.equals("es");
        
        String titulo = español ? "Solicitudes Pendientes" : "Pending Requests";

        setTitle(titulo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modeloLista = new DefaultListModel<>();
        listaSolicitudes = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaSolicitudes);

        actualizarListaSolicitudes();
        
        String aceptar = español ? "Aceptar" : "Accept";
        String rechazar = español ? "Rechazar" : "Decline";

        JPanel panelBotones = new JPanel();
        JButton btnAceptar = new JButton(aceptar);
        JButton btnRechazar = new JButton(rechazar);

        panelBotones.add(btnAceptar);
        panelBotones.add(btnRechazar);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptarSolicitud();
            }
        });

        btnRechazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechazarSolicitud();
            }
        });
    }

    private void actualizarListaSolicitudes() {
        modeloLista.clear();
        for (Amigo solicitud : usuario.getAmigos()) {
            if (!solicitud.isAceptado() && (usuario.getNombreUsuario() == null ? solicitud.getAmigo().getNombreUsuario() != null : !usuario.getNombreUsuario().equals(solicitud.getAmigo().getNombreUsuario()))) {
                modeloLista.addElement(solicitud.getAmigo().getNombreUsuario());
            }
        }
    }

    private void aceptarSolicitud() {
        String nombreAmigo = listaSolicitudes.getSelectedValue();
        System.out.println(nombreAmigo);
        if (nombreAmigo != null) {
            for (Amigo solicitud : usuario.getAmigos()) {
                if (solicitud.getAmigo().getNombreUsuario().equals(nombreAmigo) && !solicitud.isAceptado()) {
                    Control control = new Control();
                    if (control.aceptarSolicitudAmistad(usuario.getNombreUsuario(), nombreAmigo, idioma)) {
                        String aceptada = español ? "Solicitud aceptada." : "Request accepted.";
                        JOptionPane.showMessageDialog(this, aceptada);
                    }
                    actualizarListaSolicitudes();
                    amigos.actualizarListaAmigos();
                }
            }
        } else {
            String texto = español ? "Seleccione una solicitud para aceptar." : "Select a request to accept.";
            JOptionPane.showMessageDialog(this, texto);
        }
    }

    private void rechazarSolicitud() {
        String nombreAmigo = listaSolicitudes.getSelectedValue();
        if (nombreAmigo != null) {
            Amigo solicitudAEliminar = null;
            for (Amigo solicitud : usuario.getAmigos()) {
                if (solicitud.getAmigo().getNombreUsuario().equals(nombreAmigo) && !solicitud.isAceptado()) {
                    solicitudAEliminar = solicitud;
                    break;
                }
            }
            if (solicitudAEliminar != null) {
                usuario.getAmigos().remove(solicitudAEliminar);  // Elimina la solicitud de la lista
                actualizarListaSolicitudes();
                String rechazada = español ? "Solicitud rechazada." : "Request declined.";
                JOptionPane.showMessageDialog(this, rechazada);
            }
        } else {
            String texto = español ? "Seleccione una solicitud para rechazar." : "Select a request to decline.";
            JOptionPane.showMessageDialog(this, texto);
        }
    }
}
