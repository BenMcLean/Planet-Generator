package net.benmclean.planetgenerator.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ArtProcess {
    public static void main (String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new ArtProcessGame(), config);
    }

    public static class ArtProcessGame extends Game {
        @Override
        public void create() {
            System.out.println("Everything OK so far!");
            Gdx.app.exit();
        }
    }
}
