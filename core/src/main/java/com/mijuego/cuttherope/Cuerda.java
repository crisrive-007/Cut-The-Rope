/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 *
 * @author river
 */
public class Cuerda {

    private World world;
    private Dulce dulce;
    private Vector2 ganchoPos;
    private float longitud;
    private float thickness;
    private boolean isCortada;

    private Body anchorBody;
    private Body[] segmentBodies;
    private Joint[] joints;
    private int numSegments;

    // Textura para dibujar
    private Texture cuerdaTextura;

    public Cuerda(World world, Dulce dulce, Vector2 ganchoPos, float longitud, float grosor, int segmentos) {
        this.world = world;
        this.dulce = dulce;
        this.ganchoPos = ganchoPos;
        this.longitud = longitud;
        this.thickness = grosor;
        this.isCortada = false;
        this.numSegments = segmentos;
        cuerdaTextura = new Texture("cuerda.png");

        crearCuerdaFisica();
    }

    // Método para verificar si la cuerda está cortada
    public boolean isCortada() {
        return isCortada;
    }

    private void crearCuerdaFisica() {
        // Crear el cuerpo del gancho (estático)
        BodyDef anchorDef = new BodyDef();
        anchorDef.type = BodyDef.BodyType.StaticBody;
        anchorDef.position.set(ganchoPos);
        anchorBody = world.createBody(anchorDef);

        // Crear los segmentos de la cuerda
        float segmentLength = longitud / numSegments;
        segmentBodies = new Body[numSegments];
        joints = new Joint[numSegments];

        // Propiedades físicas para los segmentos
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        // Crear cada segmento de la cuerda
        for (int i = 0; i < numSegments; i++) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            // Posicionamos los segmentos en línea recta desde el gancho
            float segmentPosX = ganchoPos.x;
            float segmentPosY = ganchoPos.y - (i + 0.5f) * segmentLength;
            bodyDef.position.set(segmentPosX, segmentPosY);

            // Crear el cuerpo del segmento
            Body segmentBody = world.createBody(bodyDef);

            // Forma del segmento (rectangular y delgada)
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(thickness / 2, segmentLength / 2);
            fixtureDef.shape = shape;

            segmentBody.createFixture(fixtureDef);
            segmentBodies[i] = segmentBody;

            shape.dispose();

            // Unir este segmento al anterior o al gancho (en el caso del primer segmento)
            RevoluteJointDef jointDef = new RevoluteJointDef();

            if (i == 0) {
                // Unir al gancho
                jointDef.bodyA = anchorBody;
                jointDef.bodyB = segmentBody;
                jointDef.localAnchorA.set(0, 0);
                jointDef.localAnchorB.set(0, segmentLength / 2);
            } else {
                // Unir al segmento anterior
                jointDef.bodyA = segmentBodies[i - 1];
                jointDef.bodyB = segmentBody;
                jointDef.localAnchorA.set(0, -segmentLength / 2);
                jointDef.localAnchorB.set(0, segmentLength / 2);
            }

            jointDef.collideConnected = false;
            joints[i] = world.createJoint(jointDef);
        }

        // Conectar el último segmento al dulce
        RevoluteJointDef dulceJointDef = new RevoluteJointDef();
        dulceJointDef.bodyA = segmentBodies[numSegments - 1];
        dulceJointDef.bodyB = dulce.getBody();
        dulceJointDef.localAnchorA.set(0, -segmentLength / 2);
        dulceJointDef.localAnchorB.set(0, 0); // Punto de conexión en el dulce
        dulceJointDef.collideConnected = false;

        // Añadir este joint como el último en nuestro array 
        // (necesitamos hacer un nuevo array con un tamaño mayor)
        Joint[] newJoints = new Joint[numSegments + 1];
        System.arraycopy(joints, 0, newJoints, 0, numSegments);
        newJoints[numSegments] = world.createJoint(dulceJointDef);
        joints = newJoints;
    }

    public void update() {
        // En este método podríamos actualizar propiedades dinámicas de la cuerda
        // Por ahora no es necesario porque Box2D ya maneja la física
    }

    public void draw(SpriteBatch batch) {
        if (isCortada) {
            return;
        }

        // Dibujar cada segmento de la cuerda
        for (int i = 0; i < numSegments; i++) {
            Body body = segmentBodies[i];
            Vector2 position = body.getPosition();
            float angle = body.getAngle() * MathUtils.radiansToDegrees;

            float segmentLength = longitud / numSegments;

            // Dibujar textura con rotación
            batch.draw(cuerdaTextura,
                    position.x - thickness / 2, position.y - segmentLength / 2, // Posición
                    thickness / 2, segmentLength / 2, // Origen para rotación
                    thickness, segmentLength, // Ancho y alto
                    1, 1, // Escalado
                    angle, // Ángulo de rotación
                    0, 0, // Coordenadas en la textura
                    cuerdaTextura.getWidth(), cuerdaTextura.getHeight(), // Tamaño original de la textura
                    false, false); // Sin volteo
        }
    }

    public void cortar() {
        if (!isCortada) {
            destruirCuerda();
            isCortada = true;
        }
    }

    public boolean detectarToque(Vector2 punteroPos) {
        float segmentLength = longitud / numSegments;
        float thicknessSquared = thickness * thickness; // Evita cálculos de raíz cuadrada innecesarios

        for (Body segmentBody : segmentBodies) {
            Vector2 position = segmentBody.getPosition();
            float angle = segmentBody.getAngle();

            // Calcular puntos inicial y final del segmento
            Vector2 offset = new Vector2(0, segmentLength / 2).rotateRad(angle);
            Vector2 start = position.cpy().add(offset);
            Vector2 end = position.cpy().sub(offset);

            // Vector dirección del segmento normalizado
            Vector2 segmentDir = end.cpy().sub(start).nor();
            Vector2 toPunteroPos = punteroPos.cpy().sub(start);

            // Proyección de toPunteroPos sobre segmentDir
            float proj = MathUtils.clamp(toPunteroPos.dot(segmentDir), 0, start.dst(end));

            // Punto más cercano en el segmento
            Vector2 closestPoint = start.cpy().add(segmentDir.scl(proj));

            // Si la distancia al cuadrado es menor que el grosor², la cuerda fue tocada
            if (closestPoint.dst2(punteroPos) < thicknessSquared) {
                return true;
            }
        }
        return false;
    }

    public void dispose() {
        if (cuerdaTextura != null) {
            cuerdaTextura.dispose();
        }
    }

    // Métodos para modificar el largo de la cuerda (esto requerirá recrear la cuerda)
    public void setLongitud(float nuevaLongitud) {
        // Eliminar la cuerda actual
        destruirCuerda();

        // Actualizar longitud
        this.longitud = nuevaLongitud;

        // Crear nueva cuerda con la longitud actualizada
        crearCuerdaFisica();
    }

    private void destruirCuerda() {
        for (Joint joint : joints) {
            if (joint != null) {
                world.destroyJoint(joint);
            }
        }

        for (Body body : segmentBodies) {
            if (body != null) {
                world.destroyBody(body);
            }
        }

        if (anchorBody != null) {
            world.destroyBody(anchorBody);
        }
    }

    public Body getAnchorBody() {
        return anchorBody;
    }

    public float getLongitud() {
        return longitud;
    }
}
