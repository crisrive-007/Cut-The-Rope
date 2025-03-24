/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author river
 */
public class LoginScreen extends ScreenAdapter {

    private Stage stage;
    private Texture background, logoTexture;
    private Texture cyanBackgroundTexture;
    private Skin skin;
    private TextButton exitButton;
    private TextField usernameField, passwordField;
    private Label messageLabel;
    private Table table;
    private Table cyanPanelTable;

    public LoginScreen() {
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));
            logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png"));
        } catch (Exception e) {
            Gdx.app.error("LoginScreen", "Error cargando texturas: " + e.getMessage(), e);
            background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));
            logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png"));
        }

        // Cargar la fuente con antialiasing y mayor escala
        BitmapFont font = new BitmapFont(Gdx.files.internal("fuente.fnt"), false);

        // Estilo de las etiquetas con la nueva fuente
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        // Estilos para los textos y botones
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear tabla principal
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Crear la imagen del logo y agregarla sobre el panel cyan
        Image logoImage = new Image(logoTexture);
        logoImage.setSize(477, 209); // Ajustar el tamaño del logo si es necesario

        // Crear un panel cyan usando un Table con fondo cyan
        cyanPanelTable = new Table();

        try {
            cyanBackgroundTexture = new Texture(Gdx.files.internal("cyan_background.png"));
            Drawable cyanBackground = new TextureRegionDrawable(new TextureRegion(cyanBackgroundTexture));
            cyanPanelTable.background(cyanBackground);
        } catch (Exception e) {
            Gdx.app.error("LoginScreen", "Error al cargar fondo cyan", e);
        }

        // Establecer tamaño del panel cyan a 600x400 (más grande)
        cyanPanelTable.setSize(600, 400);

        // Crear los campos de texto y etiquetas
        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        exitButton = new TextButton("Salir", skin);

        // Etiquetas y botones
        font.setColor(Color.WHITE);
        Label usernameLabel = new Label("Usuario:", labelStyle);
        Label passwordLabel = new Label("Contraseña:", labelStyle);
        messageLabel = new Label("", labelStyle);

        TextButton loginButton = new TextButton("Iniciar sesion", skin);

        // Lógica de botones
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    validateLogin();
                } catch (IOException ex) {
                    Logger.getLogger(LoginScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Salir de la aplicación
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuPrincipal());
            }
        });
        
        stage.addActor(exitButton);
        exitButton.setSize(80, 30);
        exitButton.setPosition(10, 820);

        // Organizar los elementos dentro de la tabla del panel cyan (Login)
        cyanPanelTable.add(usernameLabel).pad(10);
        cyanPanelTable.add(usernameField).width(250).pad(10);
        cyanPanelTable.row();
        cyanPanelTable.add(passwordLabel).pad(10);
        cyanPanelTable.add(passwordField).width(250).pad(10);
        cyanPanelTable.row();
        cyanPanelTable.add(loginButton).colspan(2).pad(20);
        cyanPanelTable.row();
        cyanPanelTable.add(messageLabel).colspan(2).pad(10);

        // Agregar el logo primero en la tabla principal
        table.add(logoImage).center().padBottom(20);  // Centrar el logo y agregar un pequeño espacio debajo

        // Agregar el panel cyan debajo del logo
        table.row();  // Crear una nueva fila
        table.add(cyanPanelTable).center();  // Centrar el panel cyan

        // El logo y el panel cyan ahora están en la misma tabla, con el logo arriba del panel cyan
    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            stage.getBatch().begin();
            stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            stage.getBatch().end();

            stage.act(delta);
            stage.draw();
        } catch (Exception e) {
            Gdx.app.error("LoginScreen", "Error en render", e);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (background != null) {
            background.dispose();
        }
        if (logoTexture != null) {
            logoTexture.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (cyanBackgroundTexture != null) {
            cyanBackgroundTexture.dispose();
        }
    }

    private void validateLogin() throws IOException {
        try {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            Control control = new Control();

            if (control.iniciarSesion(user, pass)) {
                Usuario usuario = control.obtenerUsuario(user);
                if (usuario != null) {
                    messageLabel.setText("¡Inicio de sesión exitoso!");
                    messageLabel.setColor(Color.GREEN);
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuJugador(usuario));
                } else {
                    messageLabel.setText("Error al obtener datos del usuario.");
                    messageLabel.setColor(Color.RED);
                }
            } else {
                messageLabel.setText("Usuario o contraseña incorrectos.");
                messageLabel.setColor(Color.RED);
            }
        } catch (Exception e) {
            Gdx.app.error("LoginScreen", "Error en validateLogin", e);
            messageLabel.setText("Error en el sistema. Intente de nuevo.");
            messageLabel.setColor(Color.RED);
        }
    }
}
