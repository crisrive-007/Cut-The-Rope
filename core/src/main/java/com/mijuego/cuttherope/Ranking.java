/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.List;

/**
 *
 * @author river
 */
public class Ranking implements Screen {

    private Stage stage;
    private Skin skin;
    private Control controlUsuarios;
    private Usuario jugador;
    private String idioma;
    private boolean español;
    private Texture background, logoTexture;
    private Table table;

    public Ranking(Usuario jugador, String idioma) {
        this.controlUsuarios = new Control();
        this.idioma = idioma;
        this.español = idioma.equals("es");
        this.jugador = jugador;
        stage = new Stage();
        BitmapFont font = new BitmapFont(Gdx.files.internal("fuente.fnt"), false);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        table = new Table();
        table.setFillParent(true);
        table.center();

        // Cargar fondo
        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));

        // Crear el cuadro blanco central
        Drawable cuadroBlanco = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("pixel-blanco.jpg"))));
        Table cuadroRanking = new Table();
        cuadroRanking.background(cuadroBlanco);
        cuadroRanking.center(); // Centra la tabla dentro del escenario

        // Ajustar tamaño de cuadroRanking a un porcentaje de la pantalla
        cuadroRanking.setSize(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.8f);

        // Cargar logo
        logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png"));
        Image logoImage = new Image(logoTexture);
        logoImage.setSize(477, 209);
        cuadroRanking.add(logoImage).colspan(2).center().padTop(50);  // Logo centrado arriba
        cuadroRanking.row().padTop(30); // Espaciado después del logo

        // Título de la pantalla
        String title = español ? "Ranking de Jugadores" : "Ranking of Players";
        Label titleLabel = new Label(title, labelStyle);
        cuadroRanking.add(titleLabel).colspan(2).padBottom(20);
        cuadroRanking.row();

        // Crear la tabla para mostrar el ranking
        Table rankingTable = new Table();
        rankingTable.top().left();
        rankingTable.setFillParent(false); // No llena todo el ancho, solo lo necesario

        // Encabezados para el ranking
        String posicion = español ? "Posicion" : "Position";
        String jugad = español ? "jugador" : "Player";
        String puntos = español ? "Puntos" : "Points";
        rankingTable.add(new Label(posicion, labelStyle)).padRight(10);
        rankingTable.add(new Label(jugad, labelStyle)).padRight(10);
        rankingTable.add(new Label(puntos, labelStyle)).padBottom(10); // Si quieres mostrar los puntos, agrega esta columna
        rankingTable.row().padBottom(10); // Separar los encabezados de las filas

        // Obtener el ranking de jugadores
        List<String> ranking = controlUsuarios.obtenerRanking();
        int pos = 1;

        // Mostrar el ranking
        for (String usuario : ranking) {
            rankingTable.add(new Label(String.valueOf(pos), labelStyle)).padRight(10);
            rankingTable.add(new Label(usuario, labelStyle)).padRight(10);
            rankingTable.add(new Label("100", labelStyle)); // Aquí puedes reemplazar "100" por los puntos reales de cada jugador
            rankingTable.row().padBottom(10);
            pos++;
        }

        // Añadir la tabla del ranking al cuadro blanco
        cuadroRanking.add(rankingTable).colspan(2).padTop(20).expandX().center(); // Asegura que la tabla ocupe el ancho completo y esté centrada

        // Botón para volver al menú principal
        String back = español ? "Volver" : "Get Back";
        TextButton backButton = new TextButton(back, skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // Volver al menú principal
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuJugador(jugador));
            }
        });

        // Añadir el botón dentro del cuadro blanco
        cuadroRanking.row().padTop(20); // Espaciado antes del botón
        cuadroRanking.add(backButton).colspan(2).center().padTop(20); // Botón centrado dentro del cuadro blanco
        
        table.add(cuadroRanking).center();

        // Añadir el cuadro blanco al escenario
        stage.addActor(table); // Añadir el cuadroRanking al escenario
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
