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
    public final int SIZE_X = 256;
    public final int SIZE_Y = 256;
    private long SEED;
    private RNG rng;
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

    public long getSEED() {
        return SEED;
    }

    public RNG getRNG() {
        return rng;
    }

    public Palette4 getTerrainPalette() {
        return terrainPalette;
    }

    public Planet(Planet planet) {
        this(planet.SEED, planet.assets);
    }

    public Planet(long SEED, Assets assets) {
        this.SEED = SEED;
        this.assets = assets;
        rng = new StatefulRNG(new ThrustRNG(SEED));

        terrainType = Assets.Terrain.values()[rng.nextInt(Assets.Terrain.values().length)];
        biomeType = Assets.Biome.values()[rng.nextInt(Assets.Biome.values().length)];
        backgroundColor = SColor.randomColorWheel(rng, 1, 2);
        Color landColor = SColor.randomColorWheel(rng, 2, 2);
        terrainPalette = new Palette4(
                Color.BLACK,
                new Color(backgroundColor.r / 2f, backgroundColor.g / 2f, backgroundColor.b / 2f, 1f),
                new Color(landColor.r / 2f, landColor.g / 2f, landColor.b / 2f, 1f),
                landColor
        );

        if (biomeType == Assets.Biome.Hill0)
            biomePalette = terrainPalette;
        else
            biomePalette = Palette4.fade(SColor.randomColorWheel(rng, 2, 2));

        makeMap();
        atlas = packTextureAtlas();
        makeTiledMap();
    }

    private void makeMap() {
        land = new boolean[SIZE_X][];
        for (int x = 0; x < land.length; x++)
            land[x] = new boolean[SIZE_Y];

        biome = new boolean[SIZE_X][];
        for (int x = 0; x < biome.length; x++)
            biome[x] = new boolean[SIZE_Y];

        class joiseWriter implements com.sudoplay.joise.mapping.IMapping2DWriter {
            @Override
            public void write(int x, int y, double value) {
                land[x][y] = value >= 0;
                biome[x][y] = value >= 0.1;
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

        Mapping.map2DNoZ(
                MappingMode.SEAMLESS_XY,
                SIZE_X,
                SIZE_Y,
                heightTranslateDomain,
                MappingRange.DEFAULT,
                new joiseWriter(),
                null
        );
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
                    minimap.drawPixel(x, y, Color.rgba8888(biomePalette.get(
                            biomeType == Assets.Biome.Hill0 ? 2 : 3
                    )));
                else if (isLand(x, y))
                    minimap.drawPixel(x, y, Color.rgba8888(terrainPalette.get(3)));
                else
                    minimap.drawPixel(x, y, Color.rgba8888(backgroundColor));
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
