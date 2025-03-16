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

/**
 *
 * @author river
 */
public class LoginScreen extends ScreenAdapter {

    private Stage stage;
    private Texture background, logoTexture;
    private Skin skin;
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

        // Cargar imagen de fondo
        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));

        // Cargar logo
        logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png")); // Asegúrate de tener la imagen del logo en tu proyecto

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

        // Usar un color sólido para el fondo cyan
        Drawable cyanBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("cyan_background.png"))));
        cyanPanelTable.background(cyanBackground);  // Establecer el fondo cyan con una textura

        // Establecer tamaño del panel cyan a 600x400 (más grande)
        cyanPanelTable.setSize(600, 400);

        // Crear los campos de texto y etiquetas
        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

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
                validateLogin();
            }
        });

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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el fondo primero
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        // Luego dibujar la interfaz
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
        logoTexture.dispose(); // Asegúrate de liberar la memoria del logo
        skin.dispose();
    }

    private void validateLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        ControlUsuarios controlUsuarios = new ControlUsuarios("usuarios.dat");

        Usuarios usuario = controlUsuarios.buscarUsuario(user);  // Buscar al usuario por nombre

        if (usuario != null && usuario.getContraseña().equals(pass)) { // Verificar contraseña
            messageLabel.setText("¡Inicio de sesión exitoso!");
            messageLabel.setColor(Color.GREEN);

            // Proceder a la pantalla del juego
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MapaNiveles());
        } else {
            messageLabel.setText("Usuario o contraseña incorrectos.");
            messageLabel.setColor(Color.RED);
        }
    }
}
