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
public class OmNom {
    private Body body;
    private Sprite sprite;
    private Texture normalTexture;
    private Texture eatingTexture;

    public OmNom(World world, int x, int y) {
        // Cargar texturas
        normalTexture = new Texture("omnom_normal.png");
        eatingTexture = new Texture("omnom_comiendo.png");

        sprite = new Sprite(normalTexture);
        sprite.setSize(3, 3);

        // Crear cuerpo en Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

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

    public void setEatingTexture() {
        sprite.setTexture(eatingTexture);
    }

    public void setNormalTexture() {
        sprite.setTexture(normalTexture);
    }

    public void dispose() {
        normalTexture.dispose();
        eatingTexture.dispose();
    }
}
