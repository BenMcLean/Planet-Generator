package net.benmclean.planetgenerator;

import com.badlogic.gdx.Game;

public class PlanetGeneratorGame extends Game {
    public long SEED = 42; //System.currentTimeMillis();

    @Override
    public void create() {
        setScreen(new net.benmclean.planetgenerator.view.GameScreen(SEED));
    }
}
