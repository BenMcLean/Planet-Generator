package net.benmclean.planetgenerator.model;

import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

public class GameWorld {
    private Assets assets;
    private long SEED;
    private RNG rng;
    protected Coord playerCoord;
    private Planet planet;

    public GameWorld (long SEED) {
        this(SEED, new Assets());
    }

    public GameWorld(long SEED, Assets assets) {
        planet = new Planet(SEED, assets);
        setPlayer(0, 0);
    }

    public Planet getPlanet () {
        return planet;
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
}
