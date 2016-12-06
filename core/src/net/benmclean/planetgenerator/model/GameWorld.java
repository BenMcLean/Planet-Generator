package net.benmclean.planetgenerator.model;

import squidpony.squidgrid.FOV;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

public class GameWorld {

    public static final int SIZE_X = 128;
    public static final int SIZE_Y = 128;
    private long SEED;
    private RNG rng;
    private DungeonGenerator dungeonGen;
    private DungeonUtility dungeonUtil;
    private FOV fov = new FOV();
    private char[][] bareDungeon;
    protected Coord playerCoord = Coord.get(SIZE_X / 2, SIZE_Y / 2);

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
        if (x < 0) return wrapX(x + SIZE_X);
        return x % SIZE_X;
    }

    public int wrapY(int y) {
        if (y < 0) return wrapX(y + SIZE_Y);
        return y % SIZE_Y;
    }

    public GameWorld(long SEED) {
        //for (boolean i[] : known) java.util.Arrays.fill(i, true);
        rng = new RNG(SEED);
        dungeonGen = new DungeonGenerator(SIZE_X, SIZE_Y, rng);
        dungeonUtil = new DungeonUtility(rng);

        bareDungeon = dungeonGen.generate();
        char[][] copyDungeon = new char[SIZE_X][];
        for (int x = 0; x < bareDungeon.length; x++) copyDungeon[x] = bareDungeon[x].clone();

        setPlayer(SIZE_X / 2, SIZE_Y / 2);
        //setPlayer(0, 0);
//        Coord here = dungeonUtil.randomFloor(copyDungeon);
//        setPlayer(here);
//        copyDungeon[here.getX()][here.getY()] = '#';
    }

    public Boolean isWall(int x, int y) {
        if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        return bareDungeon[x][y] == '#';
    }

    public void endTurn() {
    }
}
