package net.benmclean.planetgenerator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class PlanetGeneratorGame extends Game {
    public long SEED = 42; //System.currentTimeMillis();

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        setScreen(new net.benmclean.planetgenerator.view.GameScreen(SEED));
    }
}
