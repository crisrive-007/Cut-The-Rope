/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mijuego.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

/**
 *
 * @author river
 */
public class MusicManager implements Runnable {

    private static MusicManager instance;
    private Music backgroundMusic;
    private boolean isRunning = false;
    private Thread musicThread;
    private float volume;
    private boolean isLooping = true;
    private boolean isPaused = false;
    private float position = 0f;
    private Usuario jugador;
    private Preferencias preferencias;

    public static MusicManager getInstance(Usuario jugador) {
        if (instance == null) {
            instance = new MusicManager(jugador);
        }
        return instance;
    }

    private MusicManager(Usuario jugador) {
        this.jugador = jugador;
        this.preferencias = jugador.getPreferencias();
    }

    public void initialize(String musicPath) {
        volume = preferencias.getVolumen();
        Gdx.app.postRunnable(() -> {
            try {
                FileHandle musicFile = Gdx.files.internal(musicPath);
                backgroundMusic = Gdx.audio.newMusic(musicFile);
                backgroundMusic.setLooping(isLooping);
                backgroundMusic.setVolume(volume);
            } catch (Exception e) {
                Gdx.app.error("MusicManager", "Error cargando música: " + e.getMessage());
            }
        });

        // Iniciar el hilo de música
        musicThread = new Thread(this);
        isRunning = true;
        musicThread.setDaemon(true); // El hilo terminará cuando termine la aplicación
        musicThread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                // Comprobar si la música está cargada y no está reproduciéndose
                if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
                    Gdx.app.postRunnable(() -> {
                        backgroundMusic.play();
                    });
                }

                // Esperar un poco antes de comprobar de nuevo
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                isRunning = false;
            } catch (Exception e) {
                Gdx.app.error("MusicManager", "Error en el hilo de música: " + e.getMessage());
            }
        }
    }

    public void play() {
        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.play();
                isPaused = false;
            });
        }
    }

    public void stop() {
        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.stop();
                isPaused = false;
            });
        }
    }

    public void pause() {
        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                position = backgroundMusic.getPosition();
                backgroundMusic.pause();
                isPaused = true;
            });
        }
    }

    public void resume() {
        if (backgroundMusic != null && isPaused) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.play();
                backgroundMusic.setPosition(position);
                isPaused = false;
            });
        }
    }

    public boolean isPlaying() {
        return backgroundMusic != null && backgroundMusic.isPlaying();
    }

    public boolean isPaused() {
        return isPaused;
    }

    // Método para cambiar el volumen
    public void setVolume(float volume) {
        this.volume = volume;
        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.setVolume(volume);
            });
        }
    }

    public void setLooping(boolean looping) {
        this.isLooping = looping;
        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.setLooping(looping);
            });
        }
    }

    public void dispose() {
        isRunning = false;
        if (musicThread != null) {
            musicThread.interrupt();
        }

        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.stop();
                backgroundMusic.dispose();
            });
        }
    }

    public float getPosition() {
        if (backgroundMusic != null) {
            return backgroundMusic.getPosition();
        }
        return 0;
    }

    public void setPosition(float position) {
        if (backgroundMusic != null) {
            Gdx.app.postRunnable(() -> {
                backgroundMusic.setPosition(position);
            });
        }
    }
}
