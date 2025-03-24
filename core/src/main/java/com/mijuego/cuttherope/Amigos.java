/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
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
    private Array<String> friends;
    private Usuario usuario;

    public Amigos(Usuario usuario) {
        this.usuario = usuario;
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

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        mainTable.add(new Label("Lista de Amigos", skin)).colspan(2).pad(10).row();

        friendsList = new List<>(skin);
        friendsList.setItems(friends); // Aquí muestra la lista de amigos que llenaste en el constructor

        ScrollPane scrollPane = new ScrollPane(friendsList, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);

        mainTable.add(scrollPane).width(300).height(400).colspan(2).pad(10).row();

        TextButton addButton = new TextButton("Agregar Amigo", skin);
        TextButton removeButton = new TextButton("Eliminar Amigo", skin);
        TextButton viewRequestsButton = new TextButton("Ver Solicitudes Pendientes", skin); // Nuevo botón para solicitudes

        mainTable.add(addButton).pad(10).width(150);
        mainTable.add(removeButton).pad(10).width(150).row();
        mainTable.add(viewRequestsButton).pad(10).width(300).colspan(2).row(); // Agregar el botón para solicitudes

        // Acción para el botón de ver solicitudes pendientes
        viewRequestsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new SolicitudesPendientes(usuario, Amigos.this).setVisible(true);
            }
        });

        // Acción para el botón de agregar amigo
        addButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String amigo = JOptionPane.showInputDialog("Ingrese el nombre del usuario:");
                if (amigo != null) {
                    Control control = new Control();
                    control.agregarAmigo(usuario.getNombreUsuario(), amigo);
                }
                actualizarListaAmigos();
            }
        });

        // Acción para el botón de eliminar amigo
        removeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String amigo = JOptionPane.showInputDialog("Ingrese el nombre del usuario:");
                if (amigo != null) {
                    Control control = new Control();
                    if(control.eliminarAmigo(usuario.getNombreUsuario(), amigo)) {
                        actualizarListaAmigos();
                    }
                }
                actualizarListaAmigos();
            }
        });
    }
    
    public void actualizarListaAmigos() {
        friends.clear(); // Limpiar la lista de amigos actual
        for (Amigo amigo : usuario.getAmigos()) {
            if (amigo.isAceptado()) {
                friends.add(amigo.getAmigo().getNombreUsuario()); // Agregar el nombre del amigo
            }
        }
        friendsList.setItems(friends); // Actualizar la lista visible
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
