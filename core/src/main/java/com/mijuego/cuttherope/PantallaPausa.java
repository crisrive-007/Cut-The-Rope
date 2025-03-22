/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author river
 */
public class PantallaPausa extends JFrame {

    private final Game game;

    public PantallaPausa(Usuario jugador, Game game) {
        this.game = game;

        setTitle("Panel de Pausa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Don't use EXIT_ON_CLOSE
        setSize(300, 200);
        setLocationRelativeTo(null);

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
        add(buttonPanel);

        // Acción para el botón "Menú Principal"
        mainMenuButton.addActionListener(e -> {
            // Use postRunnable to execute on the LibGDX rendering thread
            Gdx.app.postRunnable(() -> {
                game.setScreen(new MapaNiveles(jugador));
            });
            dispose(); // Close the pause window
        });

        // Acción para el botón "Reanudar"
        resumeButton.addActionListener(e -> {
            dispose(); // Just close the pause window
        });

        // Hacer visible el frame
        setVisible(true);
    }
}
