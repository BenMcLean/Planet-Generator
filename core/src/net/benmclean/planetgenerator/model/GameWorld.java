package net.benmclean.planetgenerator.model;

import net.benmclean.utils.OpenSimplexNoiseTileable3D;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

public class GameWorld {

    public static final int SIZE_X = 128;
    public static final int SIZE_Y = 128;
    private long SEED;
    private RNG rng;
    private char[][] bareDungeon;
    protected Coord playerCoord = Coord.get(SIZE_X / 2, SIZE_Y / 2);
    protected OpenSimplexNoiseTileable3D noise;

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
        //Boolean answer = isWall(getPlayerX() + dx, getPlayerY() + dy);
        //if (answer != null && !answer) {
        setPlayer(wrapX(getPlayerX() + dx), wrapY(getPlayerY() + dy));
        return true;
        //}
        //return false;
    }

    public int wrapX(int x) {
        return wrap(x, SIZE_X);
    }

    public int wrapY(int y) {
        return wrap(y, SIZE_Y);
    }

    public int wrap(int coord, int max) {
        return coord < 0 ? coord % max + max : coord % max;
        //return (coord % max + max) % max;
    }

    public GameWorld(long SEED) {
        //for (boolean i[] : known) java.util.Arrays.fill(i, true);
        rng = new RNG(SEED);
        noise = new OpenSimplexNoiseTileable3D(SEED, SIZE_X, SIZE_Y, 1);

        //setPlayer(SIZE_X / 2, SIZE_Y / 2);
        setPlayer(0, 0);
//        Coord here = dungeonUtil.randomFloor(copyDungeon);
//        setPlayer(here);
//        copyDungeon[here.getX()][here.getY()] = '#';
    }

    public Boolean isWall(int x, int y) {
        if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        //if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        return noise.eval(x/6f, y/6f, 0) < 0.25;
        //return bareDungeon[x][y] == '#';
    }

    public void endTurn() {
    }
}
