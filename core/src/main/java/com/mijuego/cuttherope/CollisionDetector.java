/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

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
    private Usuario jugador;
    private String idioma;

    private int puntos = 0;

    public CollisionDetector(World world, Dulce dulce, OmNom omNom, Rectangle[] starRectangles, boolean[] starCollected, Array<Body> bodiesToRemove, Set<Body> collidedRana, Usuario jugador, String idioma) {
        this.world = world;
        this.dulce = dulce;
        this.omNom = omNom;
        this.bodiesToRemove = bodiesToRemove;
        this.collidedStars = new HashSet<>();
        this.colisionDetectada = false;
        this.collidedRana = collidedRana;
        this.jugador = jugador;
        this.idioma = idioma;
        this.world.setContactListener(this);
        this.starRectangles = (starRectangles != null) ? starRectangles : new Rectangle[0];
        this.starCollected = (starCollected != null) ? starCollected : new boolean[0];
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) { // Verifica si el hilo fue interrumpido
            if (colisionDetectada) {
                break;
            }

            if (dulce != null && starRectangles != null) {
                for (int i = 0; i < starRectangles.length; i++) {
                    if (!starCollected[i] && dulce.getBody() != null
                            && starRectangles[i].overlaps(new Rectangle(dulce.getBody().getPosition().x - 0.5f,
                                    dulce.getBody().getPosition().y - 0.5f, 1, 1))) {
                        dulceTocoEstrella(i);
                    }
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("CollisionDetector ha sido interrumpido.");
                Thread.currentThread().interrupt(); // Restablece el estado de interrupción
                break; // Sale del bucle
            }
        }
    }

    public void dulceTocoEstrella(int starIndex) {
        if (!collidedStars.contains(starIndex)) {
            collidedStars.add(starIndex);
            starCollected[starIndex] = true;

            Gdx.app.postRunnable(() -> {
                puntos += 1;
                System.out.println("¡Colisión con estrella! Puntos: " + puntos);
            });
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
            collidedRana.add(ranaBody);

            Gdx.app.postRunnable(() -> {
                System.out.println("¡La rana se comió el dulce!");

                if (dulce != null && dulce.getBody() != null) {
                    bodiesToRemove.add(dulce.getBody());
                }

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
                        if (idioma.equals("es")) {
                            JOptionPane.showMessageDialog(null, "¡Felicidades! Has ganado.\nEstrellas obtenidas:" + puntos);
                        } else {
                            JOptionPane.showMessageDialog(null, "Congratulations! You won.\nStars obtained:" + puntos);
                        }
                        if (jugador.getProgresoJuego().getJugandonivel() == jugador.getProgresoJuego().getNivelActual()) {
                            Control control = new Control();
                            control.sumarPuntos(jugador.getNombreUsuario(), puntos);
                            control.actualizarProgresoNivel(jugador.getNombreUsuario(), control.obtenerNivelActual(jugador.getNombreUsuario()), puntos, puntos);
                            jugador.getProgresoJuego().completarNivel(jugador.getProgresoJuego().getNivelActual());
                            jugador.getProgresoJuego().setNivelActual(jugador.getProgresoJuego().getNivelActual());
                        }
                        Gdx.app.postRunnable(() -> ((Game) Gdx.app.getApplicationListener()).setScreen(new MapaNiveles(jugador, idioma)));
                    }
                }, 0.09f);
            });
        }
    }

    public int getPuntos() {
        return puntos;
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
