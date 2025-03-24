/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author river
 */
public class PantallaPausa implements Screen {

    private Stage stage;
    private Texture fondoTextura;
    private Game game;
    private Screen pantallaAnterior;
    private Usuario jugador;
    private String idioma;
    private boolean español;

    public PantallaPausa(Usuario jugador, Game game, Screen pantallaAnterior, String idioma) {
        this.game = game;
        this.pantallaAnterior = pantallaAnterior;
        this.jugador = jugador;
        this.idioma = idioma;
        this.español = idioma.equals("es");

        // Configuración de la cámara y viewport
        OrthographicCamera camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Fondo para la pantalla de pausa
        fondoTextura = new Texture("fondo_cuttherope.jpg");

        crearUI();
    }

    private void crearUI() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // If your font isn't part of the skin, load it separately
        if (!skin.has("fuente.fnt", BitmapFont.class)) {
            BitmapFont font = new BitmapFont(Gdx.files.internal("fuente.fnt"));
            skin.add("fuente.fnt", font);
        }

        // The rest of your code remains the same
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("fuente.fnt");
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.LIGHT_GRAY;
        skin.add("default", textButtonStyle);

        // Tabla para organizar los elementos de la UI
        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        
        String reanudar = español ? "Reanudar" : "Resume";
        String menu = español ? "Menu Principal" : "Main Menu";

        TextButton botonReanudar = new TextButton(reanudar, skin);
        TextButton botonMenuPrincipal = new TextButton(menu, skin);
        
        String pause = español ? "PAUSA" : "PAUSE";

        Label titulo = new Label(pause, new Label.LabelStyle(skin.getFont("fuente.fnt"), Color.WHITE));
        titulo.setFontScale(2.0f);

        // Añadir elementos a la tabla con espaciado
        tabla.add(titulo).padBottom(50).row();
        tabla.add(botonReanudar).size(250, 60).padBottom(20).row();
        tabla.add(botonMenuPrincipal).size(250, 60).padBottom(20).row();

        // Añadir listeners a los botones
        botonReanudar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reanudarJuego();
            }
        });

        botonMenuPrincipal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                irAlMenuPrincipal();
            }
        });

        // Añadir tabla al stage
        stage.addActor(tabla);
    }

    private void reanudarJuego() {
        // Volver a la pantalla anterior
        game.setScreen(pantallaAnterior);
        dispose();
    }

    private void irAlMenuPrincipal() {
        // Ir al menú principal
        game.setScreen(new MapaNiveles(jugador, idioma));
    }

    @Override
    public void show() {
        // Método llamado cuando esta pantalla se convierte en la actual
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla con un color semi-transparente para el efecto de pausa
        Gdx.gl.glClearColor(0, 0, 0, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el fondo ajustado a las dimensiones de la pantalla
        stage.getBatch().begin();
        stage.getBatch().draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        // Actualizar y dibujar la stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void pause() {
        // Método llamado cuando el juego se pausa
    }

    @Override
    public void resume() {
        // Método llamado cuando el juego se reanuda
    }

    @Override
    public void hide() {
        // Método llamado cuando esta pantalla ya no es la actual
    }

    @Override
    public void dispose() {
        stage.dispose();
        fondoTextura.dispose();
    }
}
