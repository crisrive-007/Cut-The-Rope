/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import java.util.HashMap;

/**
 *
 * @author river
 */
public class Sombrero {

    private World world;
    private Body body;
    private Texture sombreroTextura;
    private Vector2 posicion;
    private static HashMap<Sombrero, Sombrero> parejas = new HashMap<>();
    private boolean tieneDulce = false;

    public Sombrero(World world, Vector2 posicion) {
        this.world = world;
        this.posicion = posicion;
        this.sombreroTextura = new Texture("sombrero.png");
        crearSombrero();
    }

    private void crearSombrero() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(posicion);
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // Detecta colisiones pero no bloquea

        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(sombreroTextura, posicion.x - 0.5f, posicion.y - 0.5f, 1, 1);
    }

    public static void conectarSombreros(Sombrero s1, Sombrero s2) {
        parejas.put(s1, s2);
        parejas.put(s2, s1);
    }

    public void verificarDulce(Dulce dulce) {
        Vector2 dulcePos = dulce.getBody().getPosition();
        if (posicion.dst(dulcePos) < 0.5f && !tieneDulce) {
            tieneDulce = true;
            teleportarDulce(dulce);
        }
    }

    private void teleportarDulce(Dulce dulce) {
        Sombrero destino = parejas.get(this);
        if (destino != null) {
            dulce.getBody().setTransform(destino.posicion, 0);
            destino.tieneDulce = false;
        }
    }
}
