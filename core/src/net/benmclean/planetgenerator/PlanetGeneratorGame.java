package net.benmclean.planetgenerator;

import com.badlogic.gdx.Game;

public class PlanetGeneratorGame extends Game {
    public long SEED = System.currentTimeMillis();

    @Override
    public void create() {
        //setScreen(new net.benmclean.badroguelike.view.GameScreen(SEED));
    }
}
