/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author river
 */
public class Dulce implements Runnable{

    private Body body;
    private Sprite sprite;
    private Texture texture;
    private boolean cayendo;
    private boolean visible;
    private final float GRAVEDAD = 9.8f;
    private final float INTERVALO = 1 / 60f;

    public Dulce(World world, int x, int y, float PIXELS_TO_METER, Texture texture) {
        // Cargar la textura del dulce
        this.texture = texture;

        sprite = new Sprite(texture); // Asegúrate de tener la textura
        sprite.setSize(2, 2); // Ajusta el tamaño en función de los píxeles por metro
        sprite.setOriginCenter();

        // Crear cuerpo en Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);
        body.setUserData(sprite);
        shape.dispose();
        
        cayendo = false;
        visible = true;
    }

    public Body getBody() {
        return body;
    }

    public void draw(SpriteBatch batch) {
        if (visible && sprite != null) {
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
            sprite.draw(batch);
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
    
    public void cortar() {
        cayendo = true;
        Thread hiloCaida = new Thread(this);
        hiloCaida.start();
        visible = false; // Marcamos como no visible
        dispose(); // Ahora llamamos a dispose para limpiar recursos
    }

    @Override
    public void run() {
        while (cayendo && body != null) {
            actualizarPosicion();
            try {
                Thread.sleep((long) (INTERVALO * 1000)); // Esperar para simular 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void actualizarPosicion() {
        if (body != null) {
            Vector2 velocidad = body.getLinearVelocity();
            float nuevaVelocidadY = velocidad.y - GRAVEDAD * INTERVALO;

            if (body.getPosition().y > -12) { // Suelo imaginario
                body.setLinearVelocity(velocidad.x, nuevaVelocidadY);
            } else {
                cayendo = false; // Detiene la caída si llega al suelo
            }
        }
    }
}
