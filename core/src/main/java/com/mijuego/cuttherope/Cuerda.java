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
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

/**
 *
 * @author river
 */
public class Cuerda {

    private World world;
    private Body body;
    private Dulce dulce;
    private Vector2 ganchoPos;
    private float longitud;
    private float thickness;
    private RopeJoint ropeJoint;
    private boolean isCortada;

    public Cuerda(World world, Dulce dulce, Vector2 ganchoPos, float longitud, float grosor) {
        this.world = world;
        this.dulce = dulce;
        this.ganchoPos = ganchoPos;
        this.longitud = longitud;
        this.thickness = grosor;
        this.isCortada = false;
        crearCuerda();
    }

    private void crearCuerda() {
        // Crear un cuerpo estático para el gancho
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(ganchoPos);
        body = world.createBody(bodyDef);

        // Crear la cuerda con un RopeJoint, estableciendo la longitud máxima
        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = body;
        ropeJointDef.bodyB = dulce.getBody();
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = longitud;
        ropeJoint = (RopeJoint) world.createJoint(ropeJointDef);
    }

    public void update() {
        if (ropeJoint != null) {
            float distancia = ganchoPos.dst(dulce.getBody().getPosition());
            longitud = distancia;
            ropeJoint.setMaxLength(longitud);
        } else {
            cortar();
        }
    }

    public void draw(SpriteBatch batch) {
        // No dibujes la cuerda si ha sido cortada
        if (isCortada) {
            return;
        }

        Texture cuerdaTextura = new Texture("cuerda.png");

        // Posiciones de inicio y fin de la cuerda
        Vector2 dulcePos = dulce.getBody().getPosition();

        // Calcular el ángulo entre el gancho y el dulce
        float deltaX = dulcePos.x - ganchoPos.x;
        float deltaY = dulcePos.y - ganchoPos.y;
        float angle = (float) Math.atan2(deltaY, deltaX) * (180f / (float) Math.PI);

        // Calcular la longitud actual de la cuerda
        float distancia = ganchoPos.dst(dulcePos);

        // Dibujar la textura estirada con el grosor adecuado
        batch.draw(cuerdaTextura,
                ganchoPos.x - 0.11f, ganchoPos.y, // Posición inicial
                0, 0, // Origen de la rotación
                distancia, thickness, // Tamaño (largo de la cuerda y grosor deseado)
                1, 1, // Escalado
                angle, // Rotación en grados
                0, 0, // Coordenadas iniciales del recorte
                cuerdaTextura.getWidth(), cuerdaTextura.getHeight(), // Tamaño original de la textura
                false, false // Sin volteo horizontal o vertical
        );
    }

    public Body getCuerpoCuerda() {
        return body;
    }

    public void cortar() {
        if (ropeJoint != null) {
            world.destroyJoint(ropeJoint);
            ropeJoint = null; // Marcamos que la cuerda ha sido cortada
            isCortada = true;  // Indicamos que la cuerda ya ha sido cortada
        }
    }

    public boolean detectarToque(Vector2 punteroPos) {
        // Cálculo de la distancia entre el puntero y la cuerda
        Vector2 dulcePos = dulce.getBody().getPosition();

        // Calculamos la dirección de la cuerda
        Vector2 cuerdaDir = new Vector2(dulcePos.x - ganchoPos.x, dulcePos.y - ganchoPos.y).nor();

        // Proyectamos el puntero sobre la línea de la cuerda
        Vector2 toPunterPos = new Vector2(punteroPos.x - ganchoPos.x, punteroPos.y - ganchoPos.y);
        float proj = toPunterPos.dot(cuerdaDir);

        // Limitar la proyección a la longitud de la cuerda
        proj = MathUtils.clamp(proj, 0, ganchoPos.dst(dulcePos));

        // Encontramos la distancia desde el puntero a la cuerda
        Vector2 closestPoint = new Vector2(ganchoPos.x + cuerdaDir.x * proj, ganchoPos.y + cuerdaDir.y * proj);
        float distance = closestPoint.dst(punteroPos);

        // Umbral de detección (ajústalo según tus necesidades)
        float umbralToque = 0.1f; // Distancia mínima para considerar un toque

        return distance < umbralToque;
    }
}
