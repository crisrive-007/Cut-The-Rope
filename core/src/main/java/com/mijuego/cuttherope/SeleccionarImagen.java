/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author river
 */
public class SeleccionarImagen extends JFrame {

    private JLabel previewLabel;
    private JButton selectButton;
    private JButton saveButton;
    private JTextField pathTextField;
    private File selectedFile;
    //private Usuarios jugador;
    private Usuario jugador;

    public SeleccionarImagen(Usuario jugador) {
        super("Selector de Imágenes");
        this.jugador = jugador;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Crear componentes
        previewLabel = new JLabel("Vista previa de la imagen", JLabel.CENTER);
        previewLabel.setPreferredSize(new Dimension(500, 300));
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        selectButton = new JButton("Seleccionar Imagen");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarImagen();
            }
        });

        saveButton = new JButton("Guardar Imagen");
        saveButton.setEnabled(false); // Desactivado hasta que se seleccione una imagen
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarImagen(getImagePath());
            }
        });

        pathTextField = new JTextField();
        pathTextField.setEditable(false);

        // Diseño de la interfaz
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathPanel.add(new JLabel("Dirección: "), BorderLayout.WEST);
        pathPanel.add(pathTextField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(selectButton);
        buttonPanel.add(saveButton);

        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.add(pathPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(previewLabel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Imagen");

        // Filtrar solo archivos de imagen
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            mostrarImagen(selectedFile);
            pathTextField.setText(selectedFile.getAbsolutePath());
            saveButton.setEnabled(true);
            System.out.println("Imagen seleccionada: " + selectedFile.getAbsolutePath());
        }
    }

    private void mostrarImagen(File file) {
        try {
            // Cargar la imagen y redimensionarla para la vista previa
            ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
            Image originalImage = originalIcon.getImage();

            // Redimensionar manteniendo la proporción
            int maxWidth = 500;
            int maxHeight = 300;
            int width = originalImage.getWidth(null);
            int height = originalImage.getHeight(null);

            double ratio = 1.0;
            if (width > maxWidth) {
                ratio = (double) maxWidth / width;
            }
            if (height * ratio > maxHeight) {
                ratio = (double) maxHeight / height;
            }

            int newWidth = (int) (width * ratio);
            int newHeight = (int) (height * ratio);

            Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            previewLabel.setIcon(resizedIcon);
            previewLabel.setText(null); // Quitar el texto cuando hay una imagen

        } catch (Exception e) {
            previewLabel.setIcon(null);
            previewLabel.setText("Error al cargar la imagen");
            e.printStackTrace();
        }
    }

    private void guardarImagen() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero debes seleccionar una imagen",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //jugador.setAvatar(avatar);
    }
    
    private void guardarImagen(String imagePath) {
        if (selectedFile == null || imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Primero debes seleccionar una imagen",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //jugador.setAvatar(imagePath);  // Guardar la dirección de la imagen en el objeto jugador
        //jugador.setRutaAvatar(imagePath);
        Control control = new Control();
        control.actualizarAvatar(jugador.getNombreUsuario(), imagePath);
        JOptionPane.showMessageDialog(this,
                "Imagen guardada exitosamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "jpg"; // Extensión por defecto
        }
    }

    public String getImagePath() {
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return "";
    }
}
