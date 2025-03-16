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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author river
 */
public class MenuPrincipal extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private TextButton loginButton, createAccountButton, exitButton;
    private Texture background, logoTexture;

    public MenuPrincipal() {
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));
        
        logoTexture = new Texture(Gdx.files.internal("Cut_the_Rope_Logo.png"));

        // Cargar el skin (asegúrate de tener el archivo uiskin.json en tu proyecto)
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        Image logoImage = new Image(logoTexture);
        logoImage.setSize(477, 209);

        // Crear los botones
        loginButton = new TextButton("Iniciar sesion", skin);
        createAccountButton = new TextButton("Crear cuenta", skin);
        exitButton = new TextButton("Salir", skin);

        // Establecer la lógica de los botones
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla de login
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LoginScreen());
            }
        });

        createAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí puedes agregar la pantalla de creación de cuenta (aún no implementada)
                ControlUsuarios controlUsuarios;
                controlUsuarios = new ControlUsuarios("usuarios.dat");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new CrearCuenta(controlUsuarios));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Salir de la aplicación
                Gdx.app.exit();
            }
        });

        // Crear una tabla para organizar los botones
        Table table = new Table();
        table.setFillParent(true);  // Asegura que la tabla ocupe toda la pantalla
        
        table.add(logoImage).center().padBottom(20);
        table.row();

        // Agregar los botones a la tabla
        table.add(loginButton).width(300).height(60).padBottom(20).row();
        table.add(createAccountButton).width(300).height(60).padBottom(20).row();
        table.add(exitButton).width(300).height(60);

        // Añadir la tabla al stage
        stage.addActor(table);
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
}
