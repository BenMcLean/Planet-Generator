package net.benmclean.planetgenerator.model;

import com.sudoplay.joise.mapping.Mapping;
import com.sudoplay.joise.mapping.MappingMode;
import com.sudoplay.joise.mapping.MappingRange;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

public class GameWorld {

    public static final int SIZE_X = 128;
    public static final int SIZE_Y = 128;
    private long SEED;
    private RNG rng;
    private char[][] bareDungeon;
    protected Coord playerCoord = Coord.get(SIZE_X / 2, SIZE_Y / 2);
    protected boolean[][] world;
    //protected OpenSimplexNoiseTileable3D noise;

    public GameWorld(long SEED) {
        world = new boolean[SIZE_X][];
        for (int x=0; x<world.length;x++)
            world[x] = new boolean[SIZE_Y];

        class joiseWriter implements com.sudoplay.joise.mapping.Mapping2DWriter {
            @Override
            public void write(int x, int y, double value) {
                world[x][y] = value >= 0;
            }
        }

        ModuleFractal heightFractal = new ModuleFractal (ModuleFractal.FractalType.FBM,
                ModuleBasisFunction.BasisType.GRADIENT,
                ModuleBasisFunction.InterpolationType.QUINTIC);
//        heightFractal.setNumOctaves(terrainOctaves);
//        heightFractal.setFrequency(terrainFrequency);
        heightFractal.setSeed(SEED);
        ModuleFractal ridgedHeightFractal = new ModuleFractal (ModuleFractal.FractalType.RIDGEMULTI,
                ModuleBasisFunction.BasisType.SIMPLEX,
                ModuleBasisFunction.InterpolationType.QUINTIC);
//        ridgedHeightFractal.setNumOctaves(terrainRidgeOctaves);
//        ridgedHeightFractal.setFrequency(terrainFrequency);
        ridgedHeightFractal.setSeed(SEED);
        ModuleTranslateDomain heightTranslateDomain = new ModuleTranslateDomain();
        heightTranslateDomain.setSource(heightFractal);
        heightTranslateDomain.setAxisXSource(ridgedHeightFractal);

        Mapping.map2DNoZ (
                MappingMode.SEAMLESS_XY,
                SIZE_X,
                SIZE_Y,
                heightTranslateDomain,
                MappingRange.DEFAULT,
                new joiseWriter(),
                null
                );

        //for (boolean i[] : known) java.util.Arrays.fill(i, true);
        //rng = new RNG(SEED);
        //noise = new OpenSimplexNoiseTileable3D(SEED, SIZE_X, SIZE_Y, 1);

        //setPlayer(SIZE_X / 2, SIZE_Y / 2);
        setPlayer(0, 0);
//        Coord here = dungeonUtil.randomFloor(copyDungeon);
//        setPlayer(here);
//        copyDungeon[here.getX()][here.getY()] = '#';
    }

    public Boolean isWall(int x, int y) {
        //if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        //if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        //return noise.eval(wrapX(x)/6f, wrapY(y)/6f, 0) < 0.25;
        //return bareDungeon[x][y] == '#';
        return world[wrapX(x)][wrapY(y)];
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
}
