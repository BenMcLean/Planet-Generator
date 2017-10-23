package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.utils.Disposable;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;
import squidpony.squidmath.ThrustRNG;

public class Universe implements Disposable {
    private Assets assets;
    private long SEED;
    private RNG rng;
    protected Coord playerCoord;
    private Planet planet;

    public Universe (long SEED) {
        this(SEED, new Assets());
    }

    public Universe(long SEED, Assets assets) {
        this.SEED = SEED;
        this.assets = assets;
        planet = new Planet(SEED, assets);
        rng = new RNG(new ThrustRNG(SEED));
        setPlayer(0, 0);
    }

    public Universe nextPlanet() {
        planet.dispose();
        SEED = rng.nextLong();
        planet = new Planet(SEED, assets);
        return this;
    }

    public long getSEED() {
        return SEED;
    }

    public RNG getRNG() {
        return rng;
    }

    public Planet getPlanet () {
        return planet;
    }

    public Assets getAssets () {
        return assets;
    }

    public int getPlayerX() {
        return getPlayerCoord().getX();
    }

    public int getPlayerY() {
        return getPlayerCoord().getY();
    }

    public Coord getPlayerCoord() {
        return playerCoord;
    }

    public void setPlayerX(int x) {
        setPlayer(x, getPlayerY());
    }

    public void setPlayerY(int y) {
        setPlayer(getPlayerX(), y);
    }

    public void setPlayer(int x, int y) {
        setPlayer(Coord.get(x, y));
    }

    public void setPlayer(Coord coord) {
        playerCoord = coord;
    }

    public Boolean movePlayer(Direction direction) {
        return movePlayer(direction.dx(), direction.dy());
    }

    public Boolean movePlayer(int dx, int dy) {
        setPlayer(planet.wrapX(getPlayerX() + dx), planet.wrapY(getPlayerY() + dy));
        return true;
    }

    @Override
    public void dispose() {
        planet.dispose();
        assets.dispose();
    }
}