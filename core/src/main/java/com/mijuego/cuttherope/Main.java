package com.mijuego.cuttherope;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    
    private MusicManager musica;

    @Override
    public void create() {
        musica = MusicManager.getInstance();
        musica.initialize("musica/musica_juego.mp3");
        this.setScreen(new MenuPrincipal());
    }
    
    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (musica != null) {
            musica.dispose();
        }
    }
}
