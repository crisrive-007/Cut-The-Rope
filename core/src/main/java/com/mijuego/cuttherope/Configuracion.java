/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author river
 */
public class Configuracion extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private TextButton cambiarIdiomaButton, mutearMusicaButton, exitButton;
    private Texture background;
    private Usuario jugador;
    private String idioma;
    private Preferencias prefer;
    private boolean español;
    private boolean muteado;

    public Configuracion(Usuario jugador, String idioma) {
        this.jugador = jugador;
        this.idioma = idioma;
        this.español = idioma.equals("es");
        this.prefer = jugador.getPreferencias();
        this.muteado = prefer.getVolumen() == 0;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));

        // Cargar el skin (asegúrate de tener el archivo uiskin.json en tu proyecto)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear los botones
        cambiarIdiomaButton = new TextButton(getCambiarIdiomaText(), skin);
        mutearMusicaButton = new TextButton(getMutearMusicaText(), skin);
        exitButton = new TextButton(getExitText(), skin);

        // Establecer la lógica de los botones
        cambiarIdiomaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar el idioma
                Preferencias preferencias = jugador.getPreferencias();
                if (preferencias.getIdioma().equals("es")) {
                    preferencias.setIdioma("en");
                    español = false;
                } else {
                    preferencias.setIdioma("es");
                    español = true;
                }
                jugador.setPreferencias(preferencias);
                Control control = new Control();
                control.actualizarPreferencias(jugador.getNombreUsuario(), preferencias);

                // Actualizar el texto del botón
                cambiarIdiomaButton.setText(getCambiarIdiomaText());
                mutearMusicaButton.setText(getMutearMusicaText());
                exitButton.setText(getExitText());
            }
        });

        mutearMusicaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Mutear o desmutear la música
                Preferencias preferencias = jugador.getPreferencias();
                if (preferencias.getVolumen() != 0) {
                    preferencias.setVolumen(0);
                    MusicManager musicManager = MusicManager.getInstance(jugador);
                    musicManager.setVolume(0);
                    muteado = true;
                } else {
                    preferencias.setVolumen(0.5f);
                    MusicManager musicManager = MusicManager.getInstance(jugador);
                    musicManager.setVolume(0.5f);
                    muteado = false;
                }
                jugador.setPreferencias(preferencias);
                Control control = new Control();
                control.actualizarPreferencias(jugador.getNombreUsuario(), preferencias);

                // Actualizar el texto del botón
                mutearMusicaButton.setText(getMutearMusicaText());
            }
        });

        // Crear una tabla para organizar los botones
        Table table = new Table();
        table.setFillParent(true);  // Asegura que la tabla ocupe toda la pantalla

        // Agregar los botones a la tabla
        table.add(cambiarIdiomaButton).width(300).height(60).padBottom(20).row();
        table.add(mutearMusicaButton).width(300).height(60);
        exitButton.setSize(80, 30);
        exitButton.setPosition(10, 820);
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Salir de la aplicación
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuJugador(jugador));
            }
        });

        // Añadir la tabla al stage
        stage.addActor(table);
        stage.addActor(exitButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        // Dibujar la interfaz
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

    // Métodos para obtener el texto de los botones
    private String getCambiarIdiomaText() {
        return español ? "Cambiar Idioma: Espanol" : "Change Language: English";
    }

    private String getMutearMusicaText() {
        return español ? (!muteado ? "Musica: Activada" : "Musica: Desactivada") : (!muteado ? "Music: On" : "Music: Off");
    }
    
    private String getExitText() {
        return español ? "Salir" : "Exit";
    }
}
