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
public class CrearCuenta extends ScreenAdapter {

    private Stage stage;
    private Texture background, logoTexture;
    private Skin skin;
    private TextButton exitButton;
    private TextField nameField, usernameField, passwordField, confirmPasswordField;
    private Label messageLabel;
    private Table table, cyanPanelTable;
    //private ControlUsuarios controlUsuarios;
    private Control controlUsuarios;

    public CrearCuenta(Control controlUsuarios) {
        this.controlUsuarios = controlUsuarios;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar imagen de fondo
        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));

        // Cargar logo
        logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png"));

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
        
        exitButton = new TextButton("Salir", skin);

        // Crear la imagen del logo y agregarla sobre el panel cyan
        Image logoImage = new Image(logoTexture);
        logoImage.setSize(477, 209);

        // Crear un panel cyan usando un Table con fondo cyan
        cyanPanelTable = new Table();
        Drawable cyanBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("cyan_background.png"))));
        cyanPanelTable.background(cyanBackground);

        // Establecer tamaño del panel cyan a 600x400 (más grande)
        cyanPanelTable.setSize(600, 400);
        
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

        // Crear los campos de texto y etiquetas
        nameField = new TextField("", skin);
        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');

        // Etiquetas y botones
        font.setColor(Color.WHITE);
        Label nameLabel = new Label("Nombre:", labelStyle);
        Label usernameLabel = new Label("Usuario:", labelStyle);
        Label passwordLabel = new Label("Contraseña:", labelStyle);
        Label confirmPasswordLabel = new Label("Confirmar Contraseña:", labelStyle);
        messageLabel = new Label("", labelStyle);

        TextButton createAccountButton = new TextButton("Crear cuenta", skin);

        // Lógica del botón "Crear cuenta"
        createAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    createAccount();
                } catch (IOException ex) {
                    Logger.getLogger(CrearCuenta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Organizar los elementos dentro de la tabla del panel cyan (Crear cuenta)
        cyanPanelTable.add(nameLabel).pad(10);
        cyanPanelTable.add(nameField).width(250).pad(10);
        cyanPanelTable.row();
        cyanPanelTable.add(usernameLabel).pad(10);
        cyanPanelTable.add(usernameField).width(250).pad(10);
        cyanPanelTable.row();
        cyanPanelTable.add(passwordLabel).pad(10);
        cyanPanelTable.add(passwordField).width(250).pad(10);
        cyanPanelTable.row();
        cyanPanelTable.add(confirmPasswordLabel).pad(10);
        cyanPanelTable.add(confirmPasswordField).width(250).pad(10);
        cyanPanelTable.row();
        cyanPanelTable.add(createAccountButton).colspan(2).pad(20);
        cyanPanelTable.row();
        cyanPanelTable.add(messageLabel).colspan(2).pad(10);

        // Agregar el logo primero en la tabla principal
        table.add(logoImage).center().padBottom(20);

        // Agregar el panel cyan debajo del logo
        table.row();  // Crear una nueva fila
        table.add(cyanPanelTable).center();
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
        logoTexture.dispose();
        skin.dispose();
    }

    private void createAccount() throws IOException {
        String name = nameField.getText();
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            messageLabel.setText("Por favor complete todos los campos.");
            messageLabel.setColor(Color.RED);
        } else if (!pass.equals(confirmPass)) {
            messageLabel.setText("Las contraseñas no coinciden.");
            messageLabel.setColor(Color.RED);
        } else {
            // Crear un objeto Usuario con los datos ingresados
            //Usuarios nuevoUsuario = new Usuarios(user, pass, name);
            Usuario nuevoUsuario = new Usuario(user, pass, name);
            //controlUsuarios.agregarUsuario(nuevoUsuario);
            controlUsuarios.registrarUsuario(user, pass, name);

            // Mostrar mensaje de éxito
            messageLabel.setText("Cuenta creada exitosamente.");
            messageLabel.setColor(Color.GREEN);
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuJugador(nuevoUsuario));
        }
    }
}
