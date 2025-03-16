/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.Box2D;

/**
 *
 * @author river
 */
public class CutTR extends Game {

    public void create() {
        Box2D.init();
        setScreen(new Nivel1());
    }
}
