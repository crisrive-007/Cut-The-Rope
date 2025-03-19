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
    private Usuarios jugador;

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

    // Textura para la línea punteada
    private Texture lineaPunteadaTextura;

    // Botones circulares
    private Texture pauseButtonTexture;
    private Texture resetButtonTexture;
    private Circle pauseButtonCircle;
    private Circle resetButtonCircle;
    private final float BUTTON_RADIUS = 1.0f; // Radio de los botones en unidades del mundo
    private final float BUTTON_SPACING = 0.5f; // Espacio entre botones

    private int puntos = 0;
    
    public Nivel1(Usuarios jugador) {
        this.jugador = jugador;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        bodiesToRemove = new Array<Body>();

        // Crear el mundo con gravedad
        world = new World(new Vector2(0, -25f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Cargar texturas
        fondoTextura = new Texture("fondo_dulceria.jpg");
        //lineaPunteadaTextura = new Texture("linea_punteada.png");

        // Cargar texturas de los botones
        pauseButtonTexture = new Texture("boton-pausa.png");
        resetButtonTexture = new Texture("boton-reinicio.png");

        // Configurar estrellas
        starTextures = new Texture[]{
            new Texture("estrella.png"),
            new Texture("estrella.png"),
            new Texture("estrella.png")
        };
        starPositions = new Vector2[]{
            new Vector2(0, -1), // Posiciones ajustadas para que se vean como en la imagen
            new Vector2(0, -4),
            new Vector2(0, -7)
        };

        starRectangles = new Rectangle[starTextures.length];
        for (int i = 0; i < starTextures.length; i++) {
            starRectangles[i] = new Rectangle(starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
        }

        // Crear OmNom (la rana verde)
        omNom = new OmNom(world, 0, -11);

        starCollected = new boolean[starTextures.length];
        for (int i = 0; i < starCollected.length; i++) {
            starCollected[i] = false;
        }

        // Configurar cámara
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / PIXELS_TO_METER,
                Gdx.graphics.getHeight() / PIXELS_TO_METER);
        camera.position.set(0, 0, 0);
        camera.update();

        // Configurar posición de los botones circulares en la esquina superior izquierda
        float topY = camera.viewportHeight / 2 - BUTTON_RADIUS - 0.5f; // Un poco de margen desde arriba
        float leftX = -camera.viewportWidth / 2 + BUTTON_RADIUS + 0.5f; // Un poco de margen desde la izquierda

        pauseButtonCircle = new Circle(leftX, topY, BUTTON_RADIUS);
        resetButtonCircle = new Circle(leftX + BUTTON_RADIUS * 2 + BUTTON_SPACING, topY, BUTTON_RADIUS);

        // Crear dulce, gancho y cuerda
        dulce = new Dulce(world, 0, 3, PIXELS_TO_METER, new Texture("dulce1.png"));
        gancho = new Gancho(world, 0, 10);

        // Crear la cuerda con la longitud adecuada (ajustada para que se vea como en la imagen)
        float longitudCuerda = 7.0f; // Ajusta este valor según necesites
        cuerda = new Cuerda(world, dulce, gancho.getBody().getPosition(), longitudCuerda, 0.25f, 5);

        // Configurar el detector de colisiones
        collisionDetector = new CollisionDetector(world, dulce, omNom, starRectangles, starCollected, bodiesToRemove, collidedRana);
        collisionDetector.start();
        world.setContactListener(collisionDetector);

        // Configurar el procesador de entrada para detectar toques en la cuerda y botones
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector2 touchPoint = new Vector2(worldCoords.x, worldCoords.y);

                // Verificar si se tocó alguno de los botones
                if (pauseButtonCircle.contains(touchPoint.x, touchPoint.y)) {
                    pausarJuego();
                    return true;
                }

                if (resetButtonCircle.contains(touchPoint.x, touchPoint.y)) {
                    reiniciarNivel();
                    return true;
                }

                // Verificar si el toque está sobre la cuerda
                if (cuerda != null && !cuerda.isCortada() && cuerda.detectarToque(touchPoint)) {
                    cuerda.cortar();
                    return true;
                }

                return false;
            }
        });
    }

    private void pausarJuego() {
        new PantallaPausa(jugador);
    }

    private void reiniciarNivel() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new Nivel1(jugador));
    }

    private void dulceTocoEstrella(int starIndex) {
        if (!collidedStars.contains(starIndex)) {
            puntos += 1;
            System.out.println("¡Colisión con estrella! Puntos: " + puntos);
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

        // Eliminar cuerpos marcados para ser eliminados
        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();

        // Actualizar la cámara
        camera.update();

        // Dibujar el debug de Box2D (opcional, se puede comentar para la versión final)
        debugRenderer.render(world, camera.combined);

        // Configurar la matriz de proyección para dibujar sprites
        batch.setProjectionMatrix(camera.combined);

        time += delta;

        // Verificar colisiones del dulce con las estrellas
        if (dulce != null && dulce.getBody() != null) {
            Rectangle dulceRect = new Rectangle(
                    dulce.getBody().getPosition().x - 0.5f,
                    dulce.getBody().getPosition().y - 0.5f,
                    1, 1
            );

            for (int i = 0; i < starRectangles.length; i++) {
                if (!starCollected[i] && dulceRect.overlaps(starRectangles[i])) {
                    dulceTocoEstrella(i);
                }
            }
        }

        // Iniciar la renderización de sprites
        batch.begin();

        // Dibujar el fondo
        batch.draw(fondoTextura, -camera.viewportWidth / 2, -camera.viewportHeight / 2,
                camera.viewportWidth, camera.viewportHeight);

        // Dibujar la línea punteada (guía visual)
        if (cuerda != null && !cuerda.isCortada()) {
            Vector2 ganchoPos = gancho.getBody().getPosition();
            Vector2 dulcePos = dulce.getBody().getPosition();
            float lineLength = Math.abs(ganchoPos.y - dulcePos.y);

            /*batch.draw(lineaPunteadaTextura,
                    ganchoPos.x - 0.05f,
                    dulcePos.y,
                    0.1f, lineLength);*/
        }

        // Dibujar la cuerda
        if (cuerda != null) {
            cuerda.draw(batch);
        }

        // Dibujar el gancho
        gancho.draw(batch);

        // Dibujar el dulce
        if (dulce != null) {
            dulce.draw(batch);
        }

        // Dibujar las estrellas
        for (int i = 0; i < starTextures.length; i++) {
            if (!starCollected[i]) {
                batch.draw(starTextures[i], starPositions[i].x - 1, starPositions[i].y - 1, 2, 2);
            }
        }

        // Dibujar a OmNom
        omNom.draw(batch);

        // Dibujar los botones circulares
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

    private boolean tocarCuerda(float touchX, float touchY) {
        // Convertir las coordenadas de pantalla a coordenadas del mundo
        Vector2 touchPoint = new Vector2(touchX, touchY);

        // Usar el método detectarToque de la cuerda para comprobar si el toque está sobre ella
        if (cuerda != null && !cuerda.isCortada() && cuerda.detectarToque(touchPoint)) {
            cuerda.cortar();
            return true;
        }

        return false;
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / PIXELS_TO_METER;
        camera.viewportHeight = height / PIXELS_TO_METER;
        camera.update();

        // Actualizar posición de los botones al cambiar el tamaño de la pantalla
        float topY = camera.viewportHeight / 2 - BUTTON_RADIUS - 0.5f;
        float leftX = -camera.viewportWidth / 2 + BUTTON_RADIUS + 0.5f;

        pauseButtonCircle.setPosition(leftX, topY);
        resetButtonCircle.setPosition(leftX + BUTTON_RADIUS * 2 + BUTTON_SPACING, topY);
    }

    @Override
    public void pause() {
        // Puedes implementar aquí la lógica de pausa del juego
    }

    @Override
    public void resume() {
        // Lógica para reanudar el juego después de pausa
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
        // Liberar recursos de texturas
        if (fondoTextura != null) {
            fondoTextura.dispose();
        }
        if (lineaPunteadaTextura != null) {
            lineaPunteadaTextura.dispose();
        }

        // Liberar recursos de los botones
        if (pauseButtonTexture != null) {
            pauseButtonTexture.dispose();
        }
        if (resetButtonTexture != null) {
            resetButtonTexture.dispose();
        }

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

        // Destruir la cuerda
        if (cuerda != null) {
            cuerda.dispose();
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
