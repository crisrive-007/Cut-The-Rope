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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
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
public class Nivel2 implements Screen {

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
    private Cuerda cuerda1;
    private Cuerda cuerda2;
    private Cuerda cuerda3;
    private Gancho gancho1;
    private Gancho gancho2;
    private Gancho gancho3;
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

        fondoTextura = new Texture("fondo_rosquilleria.jpg");

        starTextures = new Texture[]{
            new Texture("estrella.png"),
            new Texture("estrella.png"),
            new Texture("estrella.png")
        };
        starPositions = new Vector2[]{
            new Vector2(-6, -1),
            new Vector2(-6, -4),
            new Vector2(-6, -7)
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

        dulce = new Dulce(world, 0, 1, PIXELS_TO_METER, new Texture("dulce2.png"));
        gancho1 = new Gancho(world, -5, 5); // Gancho en la parte superior
        gancho2 = new Gancho(world, 8, -1.5f);
        gancho3 = new Gancho(world, -6f, -5);
        cuerda1 = new Cuerda(world, dulce, gancho1.getBody().getPosition(), 0.1f, 0.25f); // Cuerda corta, delgada, y más rígida
        cuerda2 = new Cuerda(world, dulce, gancho2.getBody().getPosition(), 0.1f, 0.25f); // Cuerda de longitud media, grosor medio, elasticidad normal
        cuerda3 = new Cuerda(world, dulce, gancho3.getBody().getPosition(), 0.1f, 0.25f);

        collisionDetector = new CollisionDetector(world, dulce, omNom, starRectangles, starCollected, bodiesToRemove, collidedRana);
        collisionDetector.start();

        world.setContactListener(collisionDetector);

        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
                if (cuerda1.detectarToque(new Vector2(worldCoordinates.x, worldCoordinates.y))) {
                    cuerda1.cortar();
                } else if (cuerda2.detectarToque(new Vector2(worldCoordinates.x, worldCoordinates.y))) {
                    cuerda2.cortar();
                } else if (cuerda3.detectarToque(new Vector2(worldCoordinates.x, worldCoordinates.y))) {
                    cuerda3.cortar();
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
        cuerda1.update();
        cuerda2.update();
        cuerda3.update();

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

        cuerda1.draw(batch);
        cuerda2.draw(batch);
        cuerda3.draw(batch);

        // Dibujar el gancho
        gancho1.draw(batch);
        gancho2.draw(batch);
        gancho3.draw(batch);

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

    public boolean tocarCuerda(float touchX, float touchY, Cuerda cuerda) {
        if (cuerda != null) {
            Vector2 cuerdaPos = cuerda.getCuerpoCuerda().getPosition();

            float distancia = distanciaPuntoACuerda(touchX, touchY, cuerdaPos.x, cuerdaPos.y, dulce.getBody().getPosition().x, dulce.getBody().getPosition().y);

            if (distancia < 10f) {
                cuerda.cortar();
                return true;
            }

        } else {
            System.out.println("La cuerda aún no ha sido inicializada.");
        }
        return false;
    }

// Método auxiliar para calcular la distancia entre un punto y un segmento de línea (cuerda)
    private float distanciaPuntoACuerda(float px, float py, float x1, float y1, float x2, float y2) {
        // Calculamos el segmento de la cuerda
        float dx = x2 - x1;
        float dy = y2 - y1;

        // Si el segmento es un punto (x1 == x2 y y1 == y2), la distancia es la distancia entre el punto y el punto
        if (dx == 0 && dy == 0) {
            return (float) Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));
        }

        // Calculamos la proyección del punto (px, py) sobre el segmento de la cuerda
        float t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));  // Limitar t para que esté entre 0 y 1

        // Encontramos las coordenadas del punto más cercano sobre el segmento
        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;

        // Calculamos la distancia entre el punto (px, py) y el punto más cercano
        return (float) Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));
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
