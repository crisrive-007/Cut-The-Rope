/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author river
 */
public class PantallaPausa extends JFrame{

    public PantallaPausa(Usuarios jugador) {
        JFrame frame = new JFrame("Panel de Pausa");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        // Crear el panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Crear botones
        JButton mainMenuButton = new JButton("Menú Principal");
        JButton resumeButton = new JButton("Reanudar");

        // Añadir botones al panel
        buttonPanel.add(mainMenuButton);
        buttonPanel.add(resumeButton);

        // Añadir el panel al frame
        frame.add(buttonPanel);

        // Hacer visible el frame
        frame.setVisible(true);
        
        mainMenuButton.addActionListener(e -> {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MapaNiveles(jugador));
            frame.dispose();
        });

        // Acción para el botón "Reanudar"
        resumeButton.addActionListener(e -> {
            this.dispose();
        });
    }
}
