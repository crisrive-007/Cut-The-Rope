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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author river
 */
public class Tabla {

    private World world;
    private Body body;
    private Texture texturaTabla;
    private Vector2 posicion;
    private float ancho, alto, angulo;

    public Tabla(World world, Vector2 posicion, float ancho, float alto, float angulo) {
        this.world = world;
        this.posicion = posicion;
        this.ancho = ancho;
        this.alto = alto;
        this.angulo = angulo;
        this.texturaTabla = new Texture("madera.png");  // Asegúrate de tener esta textura en assets
        crearTabla();
    }

    private void crearTabla() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(posicion);
        bodyDef.angle = (float) Math.toRadians(angulo); // Convertimos el ángulo a radianes
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ancho / 2, alto / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f; // Agrega algo de fricción para el rodado
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texturaTabla,
                posicion.x - ancho / 2, posicion.y - alto / 2, // Posición
                ancho / 2, alto / 2, // Punto de origen de la rotación
                ancho, alto, // Tamaño
                1, 1, // Escala
                (float) Math.toDegrees(body.getAngle()), // Rotación en grados
                0, 0, texturaTabla.getWidth(), texturaTabla.getHeight(), false, false);
    }

    public void aplicarMovimiento(Dulce dulce) {
        Vector2 dulcePos = dulce.getBody().getPosition();
        Vector2 tablaPos = body.getPosition();

        // Calcular la dirección en la que se moverá el dulce
        float inclinacion = (float) Math.toRadians(angulo);
        float fuerzaX = (float) Math.cos(inclinacion) * 0.2f; // Ajusta la fuerza para suavidad
        float fuerzaY = (float) Math.sin(inclinacion) * 0.2f;

        // Verificar si el dulce está tocando la tabla antes de aplicar movimiento
        if (Math.abs(dulcePos.y - tablaPos.y) < alto / 2 + 0.1f && Math.abs(dulcePos.x - tablaPos.x) < ancho / 2) {
            dulce.getBody().applyForceToCenter(fuerzaX, fuerzaY, true);
        }
    }
    
    public void dispose() {
        if (texturaTabla != null) {
            texturaTabla.dispose();
            texturaTabla = null;
        }
    }
}
