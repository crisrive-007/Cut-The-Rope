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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import javax.swing.JOptionPane;

/**
 *
 * @author river
 */
public class Amigos extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private com.badlogic.gdx.scenes.scene2d.ui.List<String> friendsList;
    private TextButton exitButton;
    private Texture background;
    private Array<String> friends;
    private Usuario usuario;
    private String idioma;
    private boolean español;

    public Amigos(Usuario usuario, String idioma) {
        this.usuario = usuario;
        this.idioma = idioma;
        this.español = idioma.equals("es");
        friends = new Array<>();

        for (Amigo amigo : usuario.getAmigos()) {
            if (amigo.isAceptado()) { // Mostrar solo los amigos aceptados
                friends.add(amigo.getAmigo().getNombreUsuario());
            }
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        background = new Texture(Gdx.files.internal("fondo_cuttherope.jpg"));

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        String salir = español ? "Salir" : "Exit";
        exitButton = new TextButton(salir, skin);

        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        
        String listaAmigos = español ? "Lista de Amigos" : "Friends List";

        mainTable.add(new Label(listaAmigos, skin)).colspan(2).pad(10).row();

        friendsList = new List<>(skin);
        friendsList.setItems(friends); // Aquí muestra la lista de amigos que llenaste en el constructor

        ScrollPane scrollPane = new ScrollPane(friendsList, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);

        mainTable.add(scrollPane).width(300).height(400).colspan(2).pad(10).row();
        
        String agregar = español ? "Agregar Amigo" : "Add Friend";
        String eliminar = español ? "Eliminar Amigo" : "Delete Friend";
        String solicitudes = español ? "Ver Solicitudes Pendientes" : "View Pending Requests";

        TextButton addButton = new TextButton(agregar, skin);
        TextButton removeButton = new TextButton(eliminar, skin);
        TextButton viewRequestsButton = new TextButton(solicitudes, skin); // Nuevo botón para solicitudes

        mainTable.add(addButton).pad(10).width(150);
        mainTable.add(removeButton).pad(10).width(150).row();
        mainTable.add(viewRequestsButton).pad(10).width(300).colspan(2).row(); // Agregar el botón para solicitudes

        // Acción para el botón de ver solicitudes pendientes
        viewRequestsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new SolicitudesPendientes(usuario, Amigos.this, idioma).setVisible(true);
            }
        });

        // Acción para el botón de agregar amigo
        addButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String agregar = español ? "Agregar Amigo" : "Add Friend";
                Dialog dialog = new Dialog(agregar, skin);
                final TextField textField = new TextField("", skin);
                String ingresar = español ? "Ingrese el nombre del usuario:" : "Enter the username:";
                dialog.getContentTable().add(new Label(ingresar, skin)).row();
                dialog.getContentTable().add(textField).width(300).row();
                String aceptar = español ? "Aceptar" : "Accept";
                TextButton acceptButton = new TextButton(aceptar, skin);
                acceptButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String amigo = textField.getText();
                        if (amigo != null && !amigo.isEmpty()) {
                            Control control = new Control();
                            control.agregarAmigo(usuario.getNombreUsuario(), amigo, idioma);
                            actualizarListaAmigos();
                        }
                        dialog.hide();
                    }
                });
                String cancelar = español ? "Cancelar" : "Cancel";
                TextButton cancelButton = new TextButton(cancelar, skin);
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        dialog.hide();
                    }
                });
                dialog.getButtonTable().add(acceptButton).pad(10);
                dialog.getButtonTable().add(cancelButton).pad(10);

                dialog.show(stage);
            }
        });

        // Acción para el botón de eliminar amigo
        removeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String agregar = español ? "Agregar Amigo" : "Add Friend";
                Dialog dialog = new Dialog(agregar, skin);
                final TextField textField = new TextField("", skin);
                String ingresar = español ? "Ingrese el nombre del usuario:" : "Enter the username:";
                dialog.getContentTable().add(new Label(ingresar, skin)).row();
                dialog.getContentTable().add(textField).width(300).row();
                String aceptar = español ? "Aceptar" : "Accept";
                TextButton acceptButton = new TextButton(aceptar, skin);
                acceptButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String amigo = textField.getText();
                        if (amigo != null && !amigo.isEmpty()) {
                            Control control = new Control();
                            control.eliminarAmigo(usuario.getNombreUsuario(), amigo, idioma);
                            actualizarListaAmigos();
                        }
                        dialog.hide();
                    }
                });
                String cancelar = español ? "Cancelar" : "Cancel";
                TextButton cancelButton = new TextButton(cancelar, skin);
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        dialog.hide();
                    }
                });
                dialog.getButtonTable().add(acceptButton).pad(10);
                dialog.getButtonTable().add(cancelButton).pad(10);

                dialog.show(stage);
            }
        });
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Salir de la aplicación
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MiPerfil(usuario, idioma));
            }
        });

        stage.addActor(exitButton);
        exitButton.setSize(80, 30);
        exitButton.setPosition(10, 820);
    }
    
    public void actualizarListaAmigos() {
        friends.clear();
        if (usuario.getAmigos() != null) {
            for (Amigo amigo : usuario.getAmigos()) {
                if (amigo != null && amigo.isAceptado() && amigo.getAmigo() != null) {
                    String nombreUsuario = amigo.getAmigo().getNombreUsuario();
                    if (nombreUsuario != null) {
                        friends.add(nombreUsuario);
                    }
                }
            }
        }
        friendsList.setItems(friends);
    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.getBatch().begin();
            stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            stage.getBatch().end();
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
        } catch (Exception e) {
            Gdx.app.error("Amigos", "Error en render: " + e.getMessage(), e);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
