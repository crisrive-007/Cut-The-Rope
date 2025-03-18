/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author river
 */
public class CollisionDetector extends Thread implements ContactListener {

    private final World world;
    private boolean colisionDetectada;
    private Dulce dulce;
    private final OmNom omNom;
    private final Array<Body> bodiesToRemove;
    private final Set<Integer> collidedStars;
    private final boolean[] starCollected;
    private final Rectangle[] starRectangles;
    private Set<Body> collidedRana;

    private int puntos = 0;

    public CollisionDetector(World world, Dulce dulce, OmNom omNom, Rectangle[] starRectangles, boolean[] starCollected, Array<Body> bodiesToRemove, Set<Body> collidedRana) {
        this.world = world;
        this.dulce = dulce;
        this.omNom = omNom;
        this.bodiesToRemove = bodiesToRemove;
        this.collidedStars = new HashSet<>();
        this.starRectangles = starRectangles;
        this.starCollected = starCollected;
        this.colisionDetectada = false;
        this.collidedRana = collidedRana;
        this.world.setContactListener(this);
    }

    @Override
    public void run() {
        while (true) {
            if (colisionDetectada) {
                break;
            }

            if (dulce != null) {
                for (int i = 0; i < starRectangles.length; i++) {
                    if (!starCollected[i] && dulce.getBody() != null && starRectangles[i].overlaps(new Rectangle(dulce.getBody().getPosition().x - 0.5f, dulce.getBody().getPosition().y - 0.5f, 1, 1))) {
                        dulceTocoEstrella(i);
                    }
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void dulceTocoEstrella(int starIndex) {
        if (!collidedStars.contains(starIndex)) {
            puntos += 1;
            System.out.println("¡Colisión con estrella! Puntos: " + puntos);
            collidedStars.add(starIndex);
            starCollected[starIndex] = true;
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (dulce != null && dulceTocoOmNom(fixtureA, fixtureB)) {
            omNomComio(omNom.getBody());
        }
    }

    private boolean dulceTocoOmNom(Fixture fixtureA, Fixture fixtureB) {
        if (dulce == null) {
            return false;
        }

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Body dulceBody = dulce.getBody();
        Body omNomBody = omNom.getBody();

        if (dulceBody == null || omNomBody == null) {
            return false;
        }

        if (bodyA == dulceBody && bodyB == omNomBody) {
            return true;
        } else if (bodyB == dulceBody && bodyA == omNomBody) {
            return true;
        }
        return false;
    }

    private void omNomComio(Body ranaBody) {
        if (!collidedRana.contains(ranaBody)) {
            System.out.println("¡La rana se comió el dulce!");
            if (dulce != null && dulce.getBody() != null) {
                bodiesToRemove.add(dulce.getBody());
            }
            collidedRana.add(ranaBody);
            if (dulce != null) {
                dulce.cortar();
                dulce.dispose();
                dulce = null;
            }

            omNom.setEatingTexture();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    omNom.setNormalTexture();
                }
            }, 0.09f);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
