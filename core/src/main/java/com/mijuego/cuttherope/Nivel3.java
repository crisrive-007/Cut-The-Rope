/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author river
 */
public class Nivel3 implements Screen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Usuario jugador;
    private SpriteBatch batch;
    private Array<Body> bodiesToRemove;
    private Set<Integer> collidedStars = new HashSet<>();
    private Set<Body> collidedRana = new HashSet<>();
    private Dulce dulce;
    private Cuerda cuerda1, cuerda2, cuerda3, cuerda4;
    private Gancho gancho1, gancho2, gancho3, gancho4;
    private Tabla tabla1, tabla2;
    private CollisionDetector collisionDetector;
    private OmNom omNom;
    
    private Texture fondoTextura;
    private Texture[] starTextures;
    private Vector2[] starPositions;
    private Rectangle[] starRectangles;
    private boolean[] starCollected;
    
    private Texture pauseButtonTexture;
    private Texture resetButtonTexture;
    private Circle pauseButtonCircle;
    private Circle resetButtonCircle;
    private final float BUTTON_RADIUS = 1.0f; // Radio de los botones en unidades del mundo
    private final float BUTTON_SPACING = 0.5f;

    private int puntos = 0;

    public Nivel3(Usuario jugador) {
        this.jugador = jugador;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        bodiesToRemove = new Array<>();
        world = new World(new Vector2(0, -25f), true);
        debugRenderer = new Box2DDebugRenderer();
        
        fondoTextura = new Texture("fondo_rosquilleria.jpg");

        pauseButtonTexture = new Texture("boton-pausa.png");
        resetButtonTexture = new Texture("boton-reinicio.png");

        float SCREEN_WIDTH = 30;
        float SCREEN_HEIGHT = 15;

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
        camera.update();
        
        float topY = SCREEN_HEIGHT - BUTTON_RADIUS - 0.5f;
        float leftX = BUTTON_RADIUS - 11.5f;

        pauseButtonCircle = new Circle(leftX, topY, BUTTON_RADIUS);
        resetButtonCircle = new Circle(leftX + BUTTON_RADIUS * 2 + BUTTON_SPACING, topY, BUTTON_RADIUS);

        starTextures = new Texture[]{
            new Texture("estrella.png"),
            new Texture("estrella.png"),
            new Texture("estrella.png")
        };

        starPositions = new Vector2[]{
            new Vector2(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 + 2),
            new Vector2(SCREEN_WIDTH / 2 + 3, SCREEN_HEIGHT / 2),
            new Vector2(Math.round(SCREEN_WIDTH / 2), 3)
        };

        starRectangles = new Rectangle[starTextures.length];
        for (int i = 0; i < starTextures.length; i++) {
            starRectangles[i] = new Rectangle(starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
        }

        dulce = new Dulce(world, Math.round(SCREEN_WIDTH / 2), Math.round(SCREEN_HEIGHT - 3), 32, new Texture("dulce3.png"));

        gancho1 = new Gancho(world, SCREEN_WIDTH / 2 - 7, SCREEN_HEIGHT + 3);
        gancho2 = new Gancho(world, SCREEN_WIDTH / 2 - 3, SCREEN_HEIGHT + 2);
        gancho3 = new Gancho(world, SCREEN_WIDTH / 2 + 3, SCREEN_HEIGHT + 2);
        gancho4 = new Gancho(world, SCREEN_WIDTH / 2 + 7, SCREEN_HEIGHT + 3);

        cuerda1 = new Cuerda(world, dulce, gancho1.getBody().getPosition(), 9f, 0.25f, 5);
        cuerda2 = new Cuerda(world, dulce, gancho2.getBody().getPosition(), 6f, 0.25f, 5);
        cuerda3 = new Cuerda(world, dulce, gancho3.getBody().getPosition(), 6f, 0.25f, 5);
        cuerda4 = new Cuerda(world, dulce, gancho4.getBody().getPosition(), 9f, 0.25f, 5);

        tabla1 = new Tabla(world, new Vector2(SCREEN_WIDTH / 2 - 2, SCREEN_HEIGHT / 2 + 2), 4f, 0.5f, -40);
        tabla2 = new Tabla(world, new Vector2(SCREEN_WIDTH / 2 + 4, SCREEN_HEIGHT / 2 - 1), 4f, 0.5f, 20);

        omNom = new OmNom(world, Math.round(SCREEN_WIDTH / 2), -3);

        // ⭐ Inicializar estrellas
        starCollected = new boolean[starTextures.length];

        collisionDetector = new CollisionDetector(world, dulce, omNom, starRectangles, starCollected, bodiesToRemove, collidedRana);
        collisionDetector.start();
        world.setContactListener(collisionDetector);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector3 touchPoint3 = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector2 touchPoint = new Vector2(touchPoint3.x, touchPoint3.y);
                if (pauseButtonCircle.contains(touchPoint.x, touchPoint.y)) {
                    pausarJuego();
                    return true;
                }

                if (resetButtonCircle.contains(touchPoint.x, touchPoint.y)) {
                    reiniciarNivel();
                    return true;
                }

                if (cuerda1.detectarToque(touchPoint)) {
                    cuerda1.cortar();
                } else if (cuerda2.detectarToque(touchPoint)) {
                    cuerda2.cortar();
                } else if (cuerda3.detectarToque(touchPoint)) {
                    cuerda3.cortar();
                } else if (cuerda4.detectarToque(touchPoint)) {
                    cuerda4.cortar();
                }
                return true;
            }
        });
    }
    
    private void pausarJuego() {
        new PantallaPausa(jugador, (Game)Gdx.app.getApplicationListener());
    }

    private void reiniciarNivel() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel3(jugador));
    }

    private void dulceTocoEstrella(int starIndex) {
        float dx = dulce.getBody().getPosition().x - starPositions[starIndex].x;
        float dy = dulce.getBody().getPosition().y - starPositions[starIndex].y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);

        System.out.println("Distancia entre dulce y estrella " + starIndex + ": " + distancia);

        float radioColision = 1.0f; // Ajusta según el tamaño del dulce y la estrella

        if (distancia < radioColision && !collidedStars.contains(starIndex)) {
            puntos += 1;
            System.out.println("¡Colisión con estrella! Puntos: " + puntos);
            collidedStars.add(starIndex);
            starCollected[starIndex] = true;
            System.out.println("Se recogió la estrella " + starIndex + " -> " + starCollected[starIndex]);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        world.step(1 / 60f, 8, 3);

        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();

        cuerda1.update();
        cuerda2.update();
        cuerda3.update();
        cuerda4.update();

        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);

        if (dulce != null && dulce.getBody() != null) {
            Rectangle ballRect = new Rectangle(dulce.getBody().getPosition().x - 0.5f, dulce.getBody().getPosition().y - 0.5f, 1, 1);
            for (int i = 0; i < starRectangles.length; i++) {
                if (!starCollected[i] && ballRect.overlaps(starRectangles[i])) {
                    dulceTocoEstrella(i);
                }
            }
        }

        batch.begin();
        cuerda1.draw(batch);
        cuerda2.draw(batch);
        cuerda3.draw(batch);
        cuerda4.draw(batch);
        gancho1.draw(batch);
        gancho2.draw(batch);
        gancho3.draw(batch);
        gancho4.draw(batch);

        for (int i = 0; i < starTextures.length; i++) {
            if (!starCollected[i]) {
                batch.draw(starTextures[i], starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
            }
        }
        tabla1.draw(batch);
        tabla2.draw(batch);

        if (dulce != null) {
            dulce.draw(batch);
        }

        omNom.draw(batch);
        
        batch.draw(pauseButtonTexture,
                pauseButtonCircle.x - pauseButtonCircle.radius,
                pauseButtonCircle.y - pauseButtonCircle.radius,
                pauseButtonCircle.radius * 2,
                pauseButtonCircle.radius * 2);

        batch.draw(resetButtonTexture,
                resetButtonCircle.x - resetButtonCircle.radius,
                resetButtonCircle.y - resetButtonCircle.radius,
                resetButtonCircle.radius * 2,
                resetButtonCircle.radius * 2);
        
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (dulce != null) {
            dulce.dispose();
        }
        world.dispose();
        debugRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / 32;
        camera.viewportHeight = height / 32;
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
        if (collisionDetector != null) {
            collisionDetector.interrupt();
        }
    }
}
