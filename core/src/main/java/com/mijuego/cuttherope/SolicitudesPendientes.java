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

    public SolicitudesPendientes(Usuario usuario, Amigos amigos) {
        this.usuario = usuario;
        this.amigos = amigos;

        setTitle("Solicitudes Pendientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modeloLista = new DefaultListModel<>();
        listaSolicitudes = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaSolicitudes);

        actualizarListaSolicitudes();

        JPanel panelBotones = new JPanel();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnRechazar = new JButton("Rechazar");

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
            if (!solicitud.isAceptado()) {
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
                    if (control.aceptarSolicitudAmistad(usuario.getNombreUsuario(), nombreAmigo)) {
                        JOptionPane.showMessageDialog(this, "Solicitud aceptada.");
                    }
                    actualizarListaSolicitudes();
                    amigos.actualizarListaAmigos();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud para aceptar.");
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
                JOptionPane.showMessageDialog(this, "Solicitud rechazada.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud para rechazar.");
        }
    }
}
