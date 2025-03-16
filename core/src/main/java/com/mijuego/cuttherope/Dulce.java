/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author river
 */
public class Dulce {

    private Body body;
    private Sprite sprite;
    private Texture texture;

    public Dulce(World world, int x, int y, float PIXELS_TO_METER) {
        // Cargar la textura del dulce
        texture = new Texture("dulce1.png");

        sprite = new Sprite(new Texture("dulce1.png")); // Asegúrate de tener la textura
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
    }

    public Body getBody() {
        return body;
    }

    public void draw(SpriteBatch batch) {
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.draw(batch);
    }

    public void dispose() {
        texture.dispose();
    }
}
