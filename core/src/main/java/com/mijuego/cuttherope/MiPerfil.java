/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author river
 */
public class MiPerfil extends ScreenAdapter {

    private Stage stage;
    private Texture background, logoTexture;
    private Skin skin;
    //private Usuarios jugador;
    private Usuario jugador;

    public MiPerfil(Usuario jugador) {
        this.jugador = jugador;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));
        logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png"));

        BitmapFont font = new BitmapFont(Gdx.files.internal("fuente.fnt"), false);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Image logoImage = new Image(logoTexture);
        logoImage.setSize(477, 209);  // Tamaño del logo

        // Crear las tablas
        Table mainTable = new Table();
        mainTable.setFillParent(true); // Hace que la tabla ocupe toda la pantalla

        stage.addActor(mainTable);

        Drawable fondoPaneles = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("pixel-blanco.jpg"))));

        Table leftPanel = new Table();
        Table rightPanel = new Table();

        leftPanel.background(fondoPaneles);
        rightPanel.background(fondoPaneles);

        // Tamaño de los paneles izquierdo y derecho (asegurarse de que tengan el mismo tamaño)
        float panelWidth = Gdx.graphics.getWidth() / 2;  // Cada panel ocupa la mitad del ancho de la pantalla
        leftPanel.setSize(panelWidth, Gdx.graphics.getHeight());
        rightPanel.setSize(panelWidth, Gdx.graphics.getHeight());

        // Agregar información del jugador al panel izquierdo
        Label nombreLabel = new Label("Nombre Completo: " + jugador.getNombreCompleto(), labelStyle);
        leftPanel.add(nombreLabel).padBottom(20).row();
        //Label usuarioLabel = new Label("Usuario: " + jugador.getIdentificadorUnico(), labelStyle);
        Label usuarioLabel = new Label("Usuario: " + jugador.getNombreUsuario(), labelStyle);
        leftPanel.add(usuarioLabel).padBottom(20).row();
        Label fechaRegistroLabel = new Label("Fecha de registro: " + jugador.getFechaRegistro(), labelStyle);
        leftPanel.add(fechaRegistroLabel).padBottom(20).row();
        Label ultimaSesionLabel = new Label("Ultima sesion: " + jugador.getUltimaSesion(), labelStyle);
        leftPanel.add(ultimaSesionLabel).padBottom(20).row();
        Label progresoJuegoLabel = new Label("Progreso del juego: " + jugador.getProgresoJuego(), labelStyle);
        leftPanel.add(progresoJuegoLabel).padBottom(20).row();
        Label tiempoJugadoLabel = new Label("Tiempo total jugado: " + jugador.getTiempoTotalJugado(), labelStyle);
        leftPanel.add(tiempoJugadoLabel).padBottom(20).row();

        // Foto de perfil del jugador en el panel derecho (puedes reemplazar la imagen por la real del jugador)
        //Image perfilImage = new Image(new Texture(jugador.getAvatar()));  // Reemplazar con la imagen del jugador
        Image perfilImage = new Image(new Texture(jugador.getRutaAvatar()));
        perfilImage.setSize(20, 20); // Tamaño de la imagen de perfil

        // Botones
        TextButton cambiarImagenButton = new TextButton("Cambiar Imagen", skin);
        TextButton eliminarCuentaButton = new TextButton("Eliminar Cuenta", skin);

        // Acciones de los botones
        cambiarImagenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new SeleccionarImagen(jugador).setVisible(true);
            }
        });

        eliminarCuentaButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Aquí va la lógica para eliminar la cuenta del jugador
                System.out.println("Eliminar cuenta");
            }
        });

        // Agregar la foto de perfil y los botones al panel derecho
        rightPanel.add(perfilImage).size(220, 220).padBottom(20).row();  // Foto de perfil
        rightPanel.add(cambiarImagenButton).padBottom(10).row();  // Botón cambiar imagen
        rightPanel.add(eliminarCuentaButton).padBottom(10).row();  // Botón eliminar cuenta

        // Agregar los paneles a la tabla principal, alineados a la izquierda y derecha
        mainTable.add(logoImage).colspan(2).center().padBottom(20); // Logo en la parte superior centrado
        mainTable.row();  // Nueva fila
        mainTable.add(leftPanel).left(); 
        mainTable.add(rightPanel).right();

        // Agregar la tabla principal al stage
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  // Limpiar la pantalla

        // Dibujar fondo
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        // Dibujar el stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }
}
