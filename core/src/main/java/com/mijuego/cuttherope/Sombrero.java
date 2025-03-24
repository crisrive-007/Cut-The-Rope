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
    private String color;
    private int angulo;
    private static HashMap<Sombrero, Sombrero> parejas = new HashMap<>();
    private boolean tieneDulce = false;

    public Sombrero(World world, Vector2 posicion, String color, int angulo) {
        this.world = world;
        this.posicion = posicion;
        this.color = color;
        this.angulo = angulo;
        this.sombreroTextura = new Texture("sombrero_" + color + ".png"); // Carga la textura según el color
        crearSombrero();
    }

    private void crearSombrero() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(posicion);
        bodyDef.angle = (float) Math.toRadians(angulo); // Convierte el ángulo a radianes
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(1.5f); // Aumentamos el tamaño del sensor de colisión

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        // Obtener el tamaño de la textura del sombrero
        float anchoTextura = sombreroTextura.getWidth();
        float altoTextura = sombreroTextura.getHeight();

        // Usar el tamaño de la colisión para ajustar la escala
        float escala = body.getFixtureList().get(0).getShape().getRadius() * 2 / anchoTextura;

        // Dibuja la textura ajustada al tamaño del cuerpo
        batch.draw(
                sombreroTextura,
                posicion.x - anchoTextura * escala / 2, posicion.y - altoTextura * escala / 2,
                anchoTextura * escala / 2, altoTextura * escala / 2, // Punto de origen para rotación
                anchoTextura * escala, altoTextura * escala,
                1, 1, // Escala en X e Y
                angulo, // Ángulo de rotación
                0, 0, sombreroTextura.getWidth(), sombreroTextura.getHeight(),
                false, false
        );
    }

    public static void conectarSombreros(Sombrero s1, Sombrero s2) {
        parejas.put(s1, s2);
        parejas.put(s2, s1);
    }

    public void verificarDulce(Dulce dulce) {
        Vector2 dulcePos = dulce.getBody().getPosition();

        if (posicion.dst(dulcePos) < 1.5f && !tieneDulce) { // Aumentamos radio de detección
            tieneDulce = true;
            teleportarDulce(dulce);
        }
    }

    private void teleportarDulce(Dulce dulce) {
        Sombrero destino = parejas.get(this);

        if (destino != null && !destino.tieneDulce) {
            dulce.getBody().setTransform(destino.posicion, 0); // Teletransporta
            dulce.getBody().setLinearVelocity(0, 0); // Detiene su movimiento

            this.tieneDulce = false;  // Resetea el estado del sombrero actual
            destino.tieneDulce = true; // Marca el destino como ocupado
        }
    }

    public void dispose() {
        sombreroTextura.dispose();
    }
}
