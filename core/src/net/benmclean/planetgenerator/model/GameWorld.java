package net.benmclean.planetgenerator.model;

import squidpony.squidgrid.FOV;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;
import squidpony.squidmath.SquidID;

public class GameWorld {

    public static final int SIZE_X = 64;
    public static final int SIZE_Y = 64;
    public boolean[][] known = new boolean[SIZE_X][SIZE_Y];
    public double[][] light;
    public static final double visibilityThreshold = 0.00001d;

    private long SEED;
    private RNG rng;
    private DungeonGenerator dungeonGen;
    private DungeonUtility dungeonUtil;
    private FOV fov = new FOV();
    private char[][] bareDungeon;
    //public LazySpatialMap<Mob> mobs = new LazySpatialMap<Mob>();
    public int playerHP = 50;
    public int playerMaxHP = 100;
    protected SquidID playerSquidID;
    Coord playerCoord = Coord.get(25, 25);

    public int getPlayerX() {
        return getPlayerCoord().getX();
    }

    public int getPlayerY() {
        return getPlayerCoord().getY();
    }

    public Coord getPlayerCoord() { return playerCoord; }

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
        Boolean answer = isWall(getPlayerX() + dx, getPlayerY() + dy);
        if (answer != null && !answer) {
            setPlayer(getPlayerX() + dx, getPlayerY() + dy);
            return true;
        }
        return false;
    }

    public GameWorld(long SEED) {
        //for (boolean i[] : known) java.util.Arrays.fill(i, true);
        rng = new RNG(SEED);
        dungeonGen = new DungeonGenerator(SIZE_X, SIZE_Y, rng);
        dungeonUtil = new DungeonUtility(rng);

        bareDungeon = dungeonGen.generate();
        char[][] copyDungeon = new char[SIZE_X][];
        for (int x=0; x<bareDungeon.length; x++) copyDungeon[x] = bareDungeon[x].clone();

        Coord here = dungeonUtil.randomFloor(copyDungeon);
        playerCoord = here;
        copyDungeon[here.getX()][here.getY()] = '#';
    }

    public Boolean isWall(int x, int y) {
        if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        return bareDungeon[x][y] == '#';
    }

    public void endTurn () {}
}
