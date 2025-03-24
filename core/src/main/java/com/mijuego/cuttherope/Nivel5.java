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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author river
 */
public class Nivel5 implements Screen {
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Usuario jugador;
    private SpriteBatch batch;
    private Array<Body> bodiesToRemove;
    private Set<Integer> collidedStars = new HashSet<>();
    private Set<Body> collidedRana = new HashSet<>();
    private Dulce dulce;
    private Cuerda cuerda1, cuerda2, cuerda3, cuerda4, cuerda5, cuerda6;
    private Gancho gancho1, gancho2, gancho3, gancho4, gancho5, gancho6;
    private Tabla tabla1;
    private Sombrero sombrero1, sombrero2, sombrero3, sombrero4;
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
    private String idioma;
    private boolean español;

    public Nivel5(Usuario jugador, String idioma) {
        this.jugador = jugador;
        this.idioma = idioma;
        this.español = idioma.equals("es");
        jugador.getProgresoJuego().setJugandonivel(5);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        bodiesToRemove = new Array<>();
        world = new World(new Vector2(0, -25f), true);
        debugRenderer = new Box2DDebugRenderer();

        fondoTextura = new Texture("fondo_reposteria.jpg");

        pauseButtonTexture = new Texture("boton-pausa.png");
        resetButtonTexture = new Texture("boton-reinicio.png");

        float SCREEN_WIDTH = 30;
        float SCREEN_HEIGHT = 15;

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
        camera.update();

        // Configurar posición de los botones
        float topY = SCREEN_HEIGHT - BUTTON_RADIUS + 5.5f;
        float leftX = BUTTON_RADIUS - 11.5f;

        pauseButtonCircle = new Circle(leftX, topY, BUTTON_RADIUS);
        resetButtonCircle = new Circle(leftX + BUTTON_RADIUS * 2 + BUTTON_SPACING, topY, BUTTON_RADIUS);

        starTextures = new Texture[]{
            new Texture("estrella.png"),
            new Texture("estrella.png"),
            new Texture("estrella.png")
        };

        starPositions = new Vector2[]{
            new Vector2(SCREEN_WIDTH * 0.7f, SCREEN_HEIGHT * 0.4f),
            new Vector2(SCREEN_WIDTH * 0.27f, SCREEN_HEIGHT * 0.25f),
            new Vector2(SCREEN_WIDTH * 0.9f, SCREEN_HEIGHT * 0.4f)
        };

        starRectangles = new Rectangle[starTextures.length];
        for (int i = 0; i < starTextures.length; i++) {
            starRectangles[i] = new Rectangle(starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
        }

        dulce = new Dulce(world, Math.round(SCREEN_WIDTH * 0.5f), Math.round(SCREEN_HEIGHT * 0.75f), 32, new Texture("dulce5.png"));

        sombrero1 = new Sombrero(world, new Vector2(SCREEN_WIDTH * 0.7f, SCREEN_HEIGHT * 0.2f), "rojo", 180);
        sombrero2 = new Sombrero(world, new Vector2(SCREEN_WIDTH * 0.16f, SCREEN_HEIGHT * 0.3f), "azul", 60);
        sombrero3 = new Sombrero(world, new Vector2(SCREEN_WIDTH * 0.9f, SCREEN_HEIGHT * 0.8f), "rojo", 0);
        sombrero4 = new Sombrero(world, new Vector2(SCREEN_WIDTH * 0.9f, SCREEN_HEIGHT * 0.2f), "azul", 180);

        Sombrero.conectarSombreros(sombrero1, sombrero3);
        Sombrero.conectarSombreros(sombrero2, sombrero4);

        float centroX = SCREEN_WIDTH / 2;
        float centroY = SCREEN_HEIGHT * 0.8f;
        float radio = 5.0f; // Ajusta el radio según necesidad

        gancho1 = new Gancho(world, centroX + radio * (float) Math.cos(0), centroY + radio * (float) Math.sin(0));
        gancho2 = new Gancho(world, centroX + radio * (float) Math.cos(Math.PI / 3), centroY + radio * (float) Math.sin(Math.PI / 3));
        gancho3 = new Gancho(world, centroX + radio * (float) Math.cos(2 * Math.PI / 3), centroY + radio * (float) Math.sin(2 * Math.PI / 3));
        gancho4 = new Gancho(world, centroX + radio * (float) Math.cos(Math.PI), centroY + radio * (float) Math.sin(Math.PI));
        gancho5 = new Gancho(world, centroX + radio * (float) Math.cos(4 * Math.PI / 3), centroY + radio * (float) Math.sin(4 * Math.PI / 3));
        gancho6 = new Gancho(world, centroX + radio * (float) Math.cos(5 * Math.PI / 3), centroY + radio * (float) Math.sin(5 * Math.PI / 3));

        tabla1 = new Tabla(world, new Vector2(SCREEN_WIDTH * 0.2f, SCREEN_HEIGHT * 0.1f), 4f, 0.5f, -30);

        cuerda1 = new Cuerda(world, dulce, gancho1.getBody().getPosition(), 5f, 0.25f, 5);
        cuerda2 = new Cuerda(world, dulce, gancho2.getBody().getPosition(), 5f, 0.25f, 5);
        cuerda3 = new Cuerda(world, dulce, gancho3.getBody().getPosition(), 5f, 0.25f, 5);
        cuerda4 = new Cuerda(world, dulce, gancho4.getBody().getPosition(), 5f, 0.25f, 5);
        cuerda5 = new Cuerda(world, dulce, gancho5.getBody().getPosition(), 5f, 0.25f, 5);
        cuerda6 = new Cuerda(world, dulce, gancho6.getBody().getPosition(), 5f, 0.25f, 5);

        omNom = new OmNom(world, Math.round(SCREEN_WIDTH * 0.29f), Math.round(SCREEN_HEIGHT * 0.02f));

        // ⭐ Inicializar estrellas
        starCollected = new boolean[starTextures.length];

        collisionDetector = new CollisionDetector(world, dulce, omNom, starRectangles, starCollected, bodiesToRemove, collidedRana, jugador, idioma);
        collisionDetector.start();
        world.setContactListener(collisionDetector);

        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector2 touchPoint = new Vector2(worldCoords.x, worldCoords.y);

                if (pauseButtonCircle.contains(touchPoint.x, touchPoint.y)) {
                    pausarJuego();
                    return true;
                }

                if (resetButtonCircle.contains(touchPoint.x, touchPoint.y)) {
                    reiniciarNivel();
                    return true;
                }

                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector3 touchPoint3 = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector2 touchPoint = new Vector2(touchPoint3.x, touchPoint3.y);

                if (cuerda1.detectarToque(touchPoint)) {
                    cuerda1.cortar();
                } else if (cuerda2.detectarToque(touchPoint)) {
                    cuerda2.cortar();
                } else if (cuerda3.detectarToque(touchPoint)) {
                    cuerda3.cortar();
                } else if (cuerda4.detectarToque(touchPoint)) {
                    cuerda4.cortar();
                } else if (cuerda5.detectarToque(touchPoint)) {
                    cuerda5.cortar();
                } else if (cuerda6.detectarToque(touchPoint)) {
                    cuerda6.cortar();
                }
                return true;
            }
        });
    }
    
    private void pausarJuego() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PantallaPausa(jugador, (Game) Gdx.app.getApplicationListener(), this, idioma));
    }

    private void reiniciarNivel() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel5(jugador, idioma));
    }

    private void dulceTocoEstrella(int starIndex) {
        float dx = dulce.getBody().getPosition().x - starPositions[starIndex].x;
        float dy = dulce.getBody().getPosition().y - starPositions[starIndex].y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);

        float radioColision = 1.0f; // Ajusta según el tamaño del dulce y la estrella

        if (distancia < radioColision && !collidedStars.contains(starIndex)) {
            puntos += 1;
            collidedStars.add(starIndex);
            starCollected[starIndex] = true;
        }
    }

    private void verificarColisionDulceSombrero() {
        Sombrero[] sombreros = {sombrero1, sombrero2, sombrero3, sombrero4};

        for (Sombrero sombrero : sombreros) {
            if (sombrero != null) {
                sombrero.verificarDulce(dulce);
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        world.step(1 / 60f, 8, 3);

        verificarColisionDulceSombrero();

        cuerda1.update();
        cuerda2.update();
        cuerda3.update();
        cuerda4.update();
        cuerda5.update();
        cuerda6.update();

        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();

        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);

        if (dulce != null && dulce.getBody() != null) {
            Rectangle ballRect = new Rectangle(dulce.getBody().getPosition().x - 0.5f, dulce.getBody().getPosition().y - 0.5f, 1, 1);
            for (int i = 0; i < starRectangles.length; i++) {
                if (!starCollected[i] && ballRect.overlaps(starRectangles[i])) {
                    collisionDetector.dulceTocoEstrella(i);
                }
            }
        }

        for (int i = 0; i < starTextures.length; i++) {
            System.out.println("Posición estrella " + i + ": (" + starPositions[i].x + ", " + starPositions[i].y + ")");
        }

        batch.begin();
        
        batch.draw(fondoTextura,
                camera.position.x - camera.viewportWidth / 2,
                camera.position.y - camera.viewportHeight / 2,
                camera.viewportWidth, camera.viewportHeight);
        cuerda1.draw(batch);
        cuerda2.draw(batch);
        cuerda3.draw(batch);
        cuerda4.draw(batch);
        cuerda5.draw(batch);
        cuerda6.draw(batch);
        gancho1.draw(batch);
        gancho2.draw(batch);
        gancho3.draw(batch);
        gancho4.draw(batch);
        gancho5.draw(batch);
        gancho6.draw(batch);
        sombrero1.draw(batch);
        sombrero2.draw(batch);
        sombrero3.draw(batch);
        sombrero4.draw(batch);

        for (int i = 0; i < starTextures.length; i++) {
            if (!starCollected[i]) {
                batch.draw(starTextures[i], starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
            }
        }
        tabla1.draw(batch);

        for (int i = 0; i < starTextures.length; i++) {
            System.out.println("Estado estrella " + i + ": " + starCollected[i]);
        }

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
