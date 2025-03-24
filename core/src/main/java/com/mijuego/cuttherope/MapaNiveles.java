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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author river
 */
public class MapaNiveles extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private TextButton level1Button, level2Button, level3Button, level4Button, level5Button, exitButton;
    private Texture background;
    //private Usuarios jugador;
    private Usuario jugador;
    private String idioma;
    private boolean español;

    public MapaNiveles(Usuario jugador, String idioma) {
        this.jugador = jugador;
        this.idioma = idioma;
        this.español = idioma.equals("es");
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("mapa_cuttherope.jpg"));

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        String nivel = español ? "Nivel" : "Level";
        level1Button = new TextButton((nivel + " 1"), crearBotonConColor(Color.valueOf("ffa899")));
        level2Button = new TextButton((nivel + " 2"), crearBotonConColor(Color.valueOf("ff83eb")));
        level3Button = new TextButton((nivel + " 3"), crearBotonConColor(Color.valueOf("83cfff")));
        level4Button = new TextButton((nivel + " 4"), crearBotonConColor(Color.valueOf("673a30")));
        level5Button = new TextButton((nivel + " 5"), crearBotonConColor(Color.valueOf("ffc580")));
        String salir = español ? "Salir" : "Exit";
        exitButton = new TextButton(salir, skin);

        // Lógica de los botones para navegar a los siguientes niveles
        level1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla del Nivel 1
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel1(jugador, idioma));
            }
        });

        level2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla del Nivel 2
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel2(jugador, idioma));
            }
        });

        level3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla del Nivel 3
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel3(jugador, idioma));
            }
        });

        level4Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla del Nivel 4
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel4(jugador, idioma));
            }
        });

        level5Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla del Nivel 5
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel5(jugador, idioma));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Salir de la aplicación
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuJugador(jugador));
            }
        });

        // Agregar los botones a la tabla
        stage.addActor(level1Button);
        stage.addActor(level2Button);
        stage.addActor(level3Button);
        stage.addActor(level4Button);
        stage.addActor(level5Button);
        stage.addActor(exitButton);

        level1Button.setPosition(380, 500);
        level2Button.setPosition(725, 550);
        level3Button.setPosition(1220, 500);
        level4Button.setPosition(1200, 200);
        level5Button.setPosition(700, 175);
        exitButton.setSize(80, 30);
        exitButton.setPosition(10, 820);
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

    private Texture crearTexturaCircular(Color fillColor, Color borderColor, int diameter, int borderWidth) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        // Dibujar fondo transparente
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // Dibujar borde
        pixmap.setColor(borderColor);
        pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2);

        // Dibujar círculo interno (relleno)
        pixmap.setColor(fillColor);
        pixmap.fillCircle(diameter / 2, diameter / 2, (diameter - borderWidth * 2) / 2);

        Texture texture = new Texture(pixmap);
        pixmap.dispose(); // Liberar memoria
        return texture;
    }

    private TextButton.TextButtonStyle crearBotonConColor(Color color) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        int diameter = 100;
        int borderWidth = 5;

        Texture normalTexture = crearTexturaCircular(color, Color.WHITE, diameter, borderWidth);
        Texture pressedTexture = crearTexturaCircular(Color.valueOf("#ff6666"), Color.WHITE, diameter, borderWidth);

        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(normalTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(pressedTexture));

        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        return buttonStyle;
    }
}
