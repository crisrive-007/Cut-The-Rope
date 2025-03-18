/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author river
 */
public class Nivel1 implements Screen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;

    private final float PIXELS_TO_METER = 32;
    private SpriteBatch batch;
    private Array<Body> tmpBodies = new Array<Body>();
    private Array<Body> bodiesToRemove;
    private Set<Integer> collidedStars = new HashSet<>();
    private Set<Body> collidedRana = new HashSet<>();

    private float time = 0f;
    private float forceMagnitude = 1.0f;
    private Dulce dulce;
    private Cuerda cuerda;
    private Gancho gancho;
    private CollisionDetector collisionDetector;

    private Texture fondoTextura;
    private Texture[] starTextures;
    private Vector2[] starPositions;
    private Rectangle[] starRectangles;
    private boolean[] starCollected;

    private OmNom omNom;
    private Body bodyBox;

    private int puntos = 0;

    @Override
    public void show() {
        batch = new SpriteBatch();
        bodiesToRemove = new Array<Body>();

        world = new World(new Vector2(0, -25f), true);
        debugRenderer = new Box2DDebugRenderer();

        fondoTextura = new Texture("fondo_dulceria.jpg");

        starTextures = new Texture[]{
            new Texture("estrella.png"),
            new Texture("estrella.png"),
            new Texture("estrella.png")
        };
        starPositions = new Vector2[]{
            new Vector2(0, -1),
            new Vector2(0, -4),
            new Vector2(0, -7)
        };

        starRectangles = new Rectangle[starTextures.length];
        for (int i = 0; i < starTextures.length; i++) {
            starRectangles[i] = new Rectangle(starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
        }

        omNom = new OmNom(world, 0, -11);

        starCollected = new boolean[starTextures.length];
        for (int i = 0; i < starCollected.length; i++) {
            starCollected[i] = false;
        }

        camera = new OrthographicCamera(Gdx.graphics.getWidth() / PIXELS_TO_METER,
                Gdx.graphics.getHeight() / PIXELS_TO_METER);
        camera.position.set(0, 0, 0);
        camera.update();

        dulce = new Dulce(world, 0, 3, PIXELS_TO_METER, new Texture("dulce1.png"));
        gancho = new Gancho(world, 0, 10);
        cuerda = new Cuerda(world, dulce, gancho.getBody().getPosition(), 0.1f, 0.25f);

        collisionDetector = new CollisionDetector(world, dulce, omNom, starRectangles, starCollected, bodiesToRemove, collidedRana);
        collisionDetector.start();

        world.setContactListener(collisionDetector);

        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
                if (tocarCuerda(worldCoordinates.x, worldCoordinates.y)) {
                    if (cuerda != null) {
                        cuerda.cortar();
                    }

                }
                return true;
            }
        });
    }

    private void dulceTocoEstrella(int starIndex) {
        if (!collidedStars.contains(starIndex)) {
            puntos += 1;
            System.out.println("¡Colision con estrella! Puntos: " + puntos);
            collidedStars.add(starIndex);
            starCollected[starIndex] = true;
        }
    }

    private boolean dulceTocoOmNom(Fixture fixtureA, Fixture fixtureB) {
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        if (dulce != null && dulce.getBody() != null && bodyA == dulce.getBody() && bodyB == omNom.getBody()) {
            return true;
        } else if (dulce != null && dulce.getBody() != null && bodyB == dulce.getBody() && bodyA == omNom.getBody()) {
            return true;
        }
        return false;
    }

    private void omNomComio(Body ranaBody) {
        if (!collidedRana.contains(ranaBody)) {
            System.out.println("¡La rana se comió el dulce!");
            if (dulce != null && dulce.getBody() != null) {
                bodiesToRemove.add(dulce.getBody()); // Agregar el cuerpo para ser destruido
            }
            collidedRana.add(ranaBody);
            if (dulce != null) {
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
    public void render(float delta) {
        // Limpiar la pantalla
        ScreenUtils.clear(0, 0, 0, 1);

        // Actualizar el mundo de Box2D
        world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();

        // Actualizar la cuerda
        cuerda.update();

        // Dibujar el debug de Box2D
        debugRenderer.render(world, camera.combined);

        // Configurar la matriz de proyección para dibujar sprites
        batch.setProjectionMatrix(camera.combined);

        time += delta;

        // Cambiar la posición del dulce
        if (dulce != null && dulce.getBody() != null) {
            Rectangle ballRect = new Rectangle(dulce.getBody().getPosition().x - 0.5f, dulce.getBody().getPosition().y - 0.5f, 1, 1); // Rectángulo del dulce
            for (int i = 0; i < starRectangles.length; i++) {
                if (!starCollected[i] && ballRect.overlaps(starRectangles[i])) { // Verificar si la estrella no ha sido recolectada y hay colisión
                    dulceTocoEstrella(i);
                }
            }
        }

        // Dibujar los sprites
        batch.begin();
        //batch.draw(fondoTextura, -camera.viewportWidth / 2, -camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight);

        cuerda.draw(batch);

        // Dibujar el gancho
        gancho.draw(batch);

        if (dulce != null) {
            dulce.draw(batch); // Asegúrate de que el dulce se dibuje
        }

        for (int i = 0; i < starTextures.length; i++) {
            if (!starCollected[i]) { // Verificar si la estrella ha sido recolectada
                batch.draw(starTextures[i], starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
            }
        }

        omNom.draw(batch);

        batch.end();
    }

    private boolean tocarCuerda(float touchX, float touchY) {
        Vector3 worldCoordinates = camera.unproject(new Vector3(touchX, touchY, 0));

        // Crear un rayo desde las coordenadas del toque hacia el mundo de Box2D
        Vector2 start = new Vector2(worldCoordinates.x, worldCoordinates.y);
        Vector2 end = new Vector2(worldCoordinates.x, worldCoordinates.y + 0.1f);  // Se hace un pequeño desplazamiento para evitar un toque en el mismo lugar

        // Realizar el raycast sobre el mundo de Box2D
        RayCastCallback callback = new RayCastCallback() {
            public float reportRayFixture(Fixture fixture) {
                // Verificar si el fixture pertenece a la cuerda
                if (fixture.getBody() == cuerda.getCuerpoCuerda()) {
                    // Si el rayo golpea el cuerpo de la cuerda, cortamos la cuerda
                    cuerda.cortar();
                    dulce.cortar();
                    return 0;  // El rayo solo necesita golpear una vez, por lo que retornamos 0
                }

                return -1;  // No se encontró una colisión con la cuerda
            }

            @Override
            public float reportRayFixture(Fixture fxtr, Vector2 vctr, Vector2 vctr1, float f) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };

        // Ejecutamos el raycast
        world.rayCast(callback, start, end);

        return true;  // El toque fue procesado
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / PIXELS_TO_METER;
        camera.viewportHeight = height / PIXELS_TO_METER;
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

    @Override
    public void dispose() {
        if (starTextures != null) {
            for (Texture texture : starTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }

        // Destruir la rana
        if (omNom != null) {
            omNom.dispose();
        }

        // Destruir el mundo de Box2D
        if (world != null) {
            world.dispose();
        }

        // Destruir el renderer de depuración
        if (debugRenderer != null) {
            debugRenderer.dispose();
        }

        // Destruir la textura del dulce
        if (dulce != null) {
            dulce.dispose();
        }

        // Destruir el batch de sprites
        if (batch != null) {
            batch.dispose();
        }

        if (collisionDetector != null) {
            collisionDetector.interrupt();
        }
    }
}
