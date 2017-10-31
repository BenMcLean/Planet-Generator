package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Player extends Character {
    public Texture ship;

    public Player(long SEED, Assets assets) {
        super(SEED, assets);
        setShip(SEED);
    }

    public Player setShip(long SEED) {
        if (ship != null) ship.dispose();
        Pixmap pixmap = Assets.ship(SEED);
        ship = new Texture(pixmap);
        pixmap.dispose();
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        ship.dispose();
    }
}
