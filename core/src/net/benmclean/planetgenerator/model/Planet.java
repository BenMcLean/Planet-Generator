package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.sudoplay.joise.mapping.Mapping;
import com.sudoplay.joise.mapping.MappingMode;
import com.sudoplay.joise.mapping.MappingRange;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.Palette4;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.RNG;
import squidpony.squidmath.StatefulRNG;
import squidpony.squidmath.ThrustRNG;

import java.util.HashMap;

public class Planet implements Disposable {
    public static final int SIZE_X = 256;
    public static final int SIZE_Y = 256;
    private Assets assets;
    protected boolean[][] land;
    protected boolean[][] biome;
    private TiledMap map;
    private TextureAtlas atlas;
    private Palette4 terrainPalette;
    private Palette4 biomePalette;
    public Assets.Terrain terrainType;
    public Assets.Biome biomeType;
    public Color backgroundColor;

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public Palette4 getTerrainPalette() {
        return terrainPalette;
    }

    public static Planet randomPlanet(long SEED, Assets assets) {
        RNG rng = new StatefulRNG(new ThrustRNG(SEED));

        Assets.Terrain terrainType = rng.getRandomElement(Assets.Terrain.values());
        Assets.Biome biomeType = rng.getRandomElement(Assets.Biome.values());
        Color backgroundColor = SColor.randomColorWheel(rng, 1, 2);
        Color landColor = SColor.randomColorWheel(rng, 2, 2);
        Palette4 terrainPalette = new Palette4(
                Color.BLACK,
                new Color(backgroundColor.r / 2f, backgroundColor.g / 2f, backgroundColor.b / 2f, 1f),
                new Color(landColor.r / 2f, landColor.g / 2f, landColor.b / 2f, 1f),
                landColor
        );

        Palette4 biomePalette = biomeType == Assets.Biome.Hill0 ?
                terrainPalette
                :
                Palette4.fade(SColor.randomColorWheel(rng, 2, 2));

        double[][] noise = noise(SEED, SIZE_X, SIZE_Y);
        boolean[][] terrain = new boolean[SIZE_X][];
        boolean[][] biome = new boolean[terrain.length][];
        for (int x = 0; x < terrain.length; x++) {
            terrain[x] = new boolean[SIZE_Y];
            biome[x] = new boolean[terrain[x].length];
            for (int y = 0; y < terrain[x].length; y++) {
                terrain[x][y] = noise[x][y] >= 0;
                biome[x][y] = noise[x][y] >= 0.1;
            }
        }

        return new Planet(assets, backgroundColor, terrainType, terrainPalette, terrain, biomeType, biomePalette, biome);
    }

    public static double[][] noise(long SEED, int sizeX, int sizeY) {
        class joiseWriter implements com.sudoplay.joise.mapping.IMapping2DWriter {
            private int sizeX, sizeY;
            private double result[][];

            public joiseWriter(int sizeX, int sizeY) {
                super();
                this.sizeX = sizeX;
                this.sizeY = sizeY;
                result = new double[sizeX][];
                for (int x = 0; x < result.length; x++)
                    result[x] = new double[sizeY];
            }

            @Override
            public void write(int x, int y, double value) {
                result[x][y] = value;
            }

            public double[][] read() {
                return result;
            }
        }

        ModuleFractal heightFractal = new ModuleFractal(ModuleFractal.FractalType.FBM,
                ModuleBasisFunction.BasisType.GRADIENT,
                ModuleBasisFunction.InterpolationType.QUINTIC);
//        heightFractal.setNumOctaves(terrainOctaves);
//        heightFractal.setFrequency(terrainFrequency);
        heightFractal.setSeed(SEED);
        ModuleFractal ridgedHeightFractal = new ModuleFractal(ModuleFractal.FractalType.RIDGEMULTI,
                ModuleBasisFunction.BasisType.SIMPLEX,
                ModuleBasisFunction.InterpolationType.QUINTIC);
//        ridgedHeightFractal.setNumOctaves(terrainRidgeOctaves);
//        ridgedHeightFractal.setFrequency(terrainFrequency);
        ridgedHeightFractal.setSeed(SEED);
        ModuleTranslateDomain heightTranslateDomain = new ModuleTranslateDomain();
        heightTranslateDomain.setSource(heightFractal);
        heightTranslateDomain.setAxisXSource(ridgedHeightFractal);

        joiseWriter writer = new joiseWriter(sizeX, sizeY);

        Mapping.map2DNoZ(
                MappingMode.SEAMLESS_XY,
                sizeX,
                sizeY,
                heightTranslateDomain,
                MappingRange.DEFAULT,
                writer,
                null
        );

        return writer.read();
    }

    public Planet(Assets assets, Color backgroundColor, Assets.Terrain terrainType, Palette4 terrainPalette, boolean[][] terrain, Assets.Biome biomeType, Palette4 biomePalette, boolean[][] biome) {
        this.assets = assets;
        this.backgroundColor = backgroundColor;
        this.terrainType = terrainType;
        this.terrainPalette = terrainPalette;
        this.land = terrain;
        this.biomeType = biomeType;
        this.biomePalette = biomePalette;
        this.biome = biome;
        atlas = packTextureAtlas();
        makeTiledMap();
    }

    protected static void packInCells(HashMap<String, TiledMapTileLayer.Cell> cells, TextureAtlas raw, String category) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                cells.put(
                        region.name,
                        new TiledMapTileLayer.Cell().setTile(
                                new StaticTiledMapTile(region)
                        )
                );
    }

    protected void makeTiledMap() {
        HashMap<String, TiledMapTileLayer.Cell> cells = new HashMap<String, TiledMapTileLayer.Cell>();
        packInCells(cells, atlas, "utils");
        packInCells(cells, atlas, "terrain/" + terrainType);
        packInCells(cells, atlas, "biomes/" + biomeType);

        if (map != null) map.dispose();
        map = new TiledMap();
        TiledMapTileLayer[] layers = new TiledMapTileLayer[3];
        Planet.CoordCheckerInterface landChecker = new Planet.CoordCheckerInterface() {
            @Override
            public boolean where(int x, int y) {
                return isLand(x, y);
            }
        };
        Planet.CoordCheckerInterface biomeChecker = new Planet.CoordCheckerInterface() {
            @Override
            public boolean where(int x, int y) {
                return isBiome(x, y);
            }
        };
        for (int x = 0; x < layers.length; x++)
            layers[x] = new TiledMapTileLayer(SIZE_X, SIZE_Y, Assets.TILE_WIDTH, Assets.TILE_HEIGHT);
        String name = "";
        for (int x = 0; x < SIZE_X; x++)
            for (int y = 0; y < SIZE_Y; y++) {
                if (isLand(x, y))
                    layers[0].setCell(x, y, cells.get("terrain/" + terrainType));
                else if (landChecker.isEdge(x, y))
                    layers[0].setCell(x, y, cells.get(terrainName(x, y, landChecker)));

                if (isBiome(x, y)) layers[1].setCell(x, y, cells.get(biomeName(x, y, biomeChecker)));
            }
        for (MapLayer layer : layers)
            map.getLayers().add(layer);
    }

    public static TiledMapTileLayer.Cell makeCell(TiledMapTile tile) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);
        return cell;
    }

    public TiledMap getMap() {
        return map;
    }

    public boolean isLand(int x, int y) {
        //if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        //if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        //return noise.eval(wrapX(x)/6f, wrapY(y)/6f, 0) < 0.25;
        //return bareDungeon[x][y] == '#';
        return land[wrapX(x)][wrapY(y)];
    }

    public boolean isBiome(int x, int y) {
        //if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        //if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        //return noise.eval(wrapX(x)/6f, wrapY(y)/6f, 0) < 0.25;
        //return bareDungeon[x][y] == '#';
        return biome[wrapX(x)][wrapY(y)];
    }

    private TextureAtlas packTextureAtlas() {
        AtlasRepacker repacker = new AtlasRepacker(assets.atlas)
                .pack("utils")
                .pack("terrain/" + terrainType, terrainPalette)
                .pack("biomes/" + biomeType, biomePalette);

        Pixmap minimap = new Pixmap(SIZE_X, SIZE_Y, Pixmap.Format.RGBA8888);
        for (int y = 0; y < SIZE_Y; y++)
            for (int x = 0; x < SIZE_X; x++)
                if (isBiome(x, y))
                    minimap.drawPixel(x, SIZE_Y - y - 1, Color.rgba8888(biomePalette.get(
                            biomeType == Assets.Biome.Hill0 ? 2 : 3
                    )));
                else if (isLand(x, y))
                    minimap.drawPixel(x, SIZE_Y - y - 1, Color.rgba8888(terrainPalette.get(3)));
                else
                    minimap.drawPixel(x, SIZE_Y - y - 1, Color.rgba8888(backgroundColor));
        repacker.pack("procgen/minimap", minimap);
        minimap.dispose();

        TextureAtlas textureAtlas = repacker.generateTextureAtlas();
        repacker.dispose();
        return textureAtlas;
    }

    /**
     * Does not dispose Assets!
     */
    @Override
    public void dispose() {
        terrainPalette.dispose();
        biomePalette.dispose();
        map.dispose();
        atlas.dispose();
    }

    public static abstract class CoordCheckerInterface {
        public abstract boolean where(int x, int y);

        public boolean isEdge(int x, int y) {
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (where(x + dx, y + dy))
                        return true;
            return false;
        }
    }

    public String terrainName(int x, int y, CoordCheckerInterface where) {
        return terrainName("terrain/" + terrainType + "Shore", x, y, where);
    }

    public String terrainName(String name, int x, int y, CoordCheckerInterface where) {
        if (where.where(x, y + 1)) name += "N";
        if (where.where(x, y - 1)) name += "S";
        if (where.where(x + 1, y)) name += "E";
        if (where.where(x - 1, y)) name += "W";
        if (!where.where(x + 1, y) && !where.where(x, y + 1) && where.where(x + 1, y + 1)) name += "NEC";
        if (!where.where(x + 1, y) && !where.where(x, y - 1) && where.where(x + 1, y - 1)) name += "SEC";
        if (!where.where(x - 1, y) && !where.where(x, y - 1) && where.where(x - 1, y - 1)) name += "SWC";
        if (!where.where(x - 1, y) && !where.where(x, y + 1) && where.where(x - 1, y + 1)) name += "NWC";
        if (atlas.findRegion(name) == null) return "utils/test";
        return name;
    }

    public String biomeName(int x, int y, CoordCheckerInterface where) {
        return biomeName("biomes/" + biomeType, x, y, where);
    }

    public String biomeName(String name, int x, int y, CoordCheckerInterface where) {
        if (!where.where(x, y - 1)) name += "N";
        if (!where.where(x, y + 1)) name += "S";
        if (!where.where(x - 1, y)) name += "E";
        if (!where.where(x + 1, y)) name += "W";
        if (atlas.findRegion(name) == null) return "utils/test";
        return name;
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
