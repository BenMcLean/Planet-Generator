package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
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
import net.benmclean.utils.Palette4;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

import java.util.HashMap;

public class Planet implements Disposable {
    public final int SIZE_X = 256;
    public final int SIZE_Y = 256;
    private long SEED;
    private RNG rng;
    private Assets assets;
    protected Coord playerCoord = Coord.get(SIZE_X / 2, SIZE_Y / 2);
    protected boolean[][] world;
    private TiledMap map;
    private TextureAtlas atlas;
    private Palette4 terrainPalette;
    public String terrainName;
    public Color backgroundColor;

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public long getSEED() {
        return SEED;
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
        rng = new RNG(SEED);

        switch (rng.nextInt(3)) {
            case 1:
                terrainName = "Sand";
                break;
            case 2:
                terrainName = "Snow";
                break;
            default:
                terrainName = "Grass";
                break;
        }

//        backgroundColor = new Color(15f / 255f, 215f / 255f, 1f, 1f);
        backgroundColor = SColor.randomColorWheel(rng, 2, 2);
        Color land = SColor.randomColorWheel(rng, 2, 2);
        terrainPalette = new Palette4(
                Color.BLACK,
                new Color(backgroundColor.r / 2f, backgroundColor.g / 2f, backgroundColor.b / 2f, 1f),
                new Color(land.r / 2f, land.g / 2f, land.b / 2f, 1f),
                land
        );
        atlas = packTextureAtlas();

        makeMap();
    }

    private void makeMap() {
        world = new boolean[SIZE_X][];
        for (int x = 0; x < world.length; x++)
            world[x] = new boolean[SIZE_Y];

        class joiseWriter implements com.sudoplay.joise.mapping.Mapping2DWriter {
            @Override
            public void write(int x, int y, double value) {
                world[x][y] = value >= 0;
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

        makeTiledMap();
    }

    protected static void packInTiles(HashMap<String, StaticTiledMapTile> tiles, TextureAtlas raw, String category) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                tiles.put(region.name, new StaticTiledMapTile(region));
    }

    protected void makeTiledMap() {
        HashMap<String, StaticTiledMapTile> tiles = new HashMap<String, StaticTiledMapTile>();
        packInTiles(tiles, atlas, "utils" + terrainName);
        packInTiles(tiles, atlas, "terrain/" + terrainName);

        if (map != null) map.dispose();
        map = new TiledMap();
        TiledMapTileLayer[] layers = new TiledMapTileLayer[2];
        Planet.CoordCheckerInterface coordChecker = new Planet.CoordCheckerInterface() {
            @Override
            public boolean where(int x, int y) {
                return isWall(x, y);
            }
        };
        for (int x = 0; x < layers.length; x++)
            layers[x] = new TiledMapTileLayer(SIZE_X, SIZE_Y, Assets.TILE_WIDTH, Assets.TILE_HEIGHT);
        String name = "";
        for (int x = 0; x < SIZE_X; x++)
            for (int y = 0; y < SIZE_Y; y++) {
                StaticTiledMapTile tile = null;
                Boolean answer = isWall(x, y);
                if (answer != null && !answer) {
                    //tile = new StaticTiledMapTile(atlas.findRegion("terrain/" + terrainName));
                    tile = tiles.get("terrain/" + terrainName);
                    layers[1].setCell(x, y, makeCell(tile));
                } else if (answer != null) {
                    tile = tiles.get(terrainName(x, y, coordChecker));
                    layers[0].setCell(x, y, makeCell(tile));
                } else throw new NullPointerException();
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

    public boolean isWall(int x, int y) {
        //if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        //if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        //return noise.eval(wrapX(x)/6f, wrapY(y)/6f, 0) < 0.25;
        //return bareDungeon[x][y] == '#';
        return world[wrapX(x)][wrapY(y)];
    }

    private TextureAtlas packTextureAtlas() {
        PixmapPacker packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false);
        packIn("utils", assets.atlas, packer);
        packIn("terrain/" + terrainName, assets.atlas, packer, terrainPalette);
        return packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
    }

    public static void packIn(String category, TextureAtlas raw, PixmapPacker packer) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                packIn(region, packer);
    }

    public static void packIn(TextureAtlas.AtlasRegion region, PixmapPacker packer) {
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        Pixmap result = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
        for (int x = 0; x < region.getRegionWidth(); x++)
            for (int y = 0; y < region.getRegionHeight(); y++)
                result.drawPixel(x, y, pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));

        packer.pack(region.toString(), result);
        texture.dispose();
    }

    public static void packIn(String category, TextureAtlas raw, PixmapPacker packer, Palette4 palette) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                packIn(region, packer, palette);
    }

    public static void packIn(TextureAtlas.AtlasRegion region, PixmapPacker packer, Palette4 palette) {
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap result = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
        Color color = new Color();
        for (int x = 0; x < region.getRegionWidth(); x++)
            for (int y = 0; y < region.getRegionHeight(); y++) {
                color.set(pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));
                if (color.a > .05)
                    result.drawPixel(x, y, Color.rgba8888(palette.get((int) (color.r * 3.9999))));
                else
                    result.drawPixel(x, y, Assets.transparent);
            }
        packer.pack(region.toString(), result);
        texture.dispose();
    }

    /**
     * Does not dispose Assets!
     */
    @Override
    public void dispose() {
        atlas.dispose();
        terrainPalette.dispose();
    }

    public static abstract class CoordCheckerInterface {
        public abstract boolean where(int x, int y);
    }

    public String terrainName(int x, int y, CoordCheckerInterface where) {
        return terrainName("terrain/" + terrainName + "Shore", x, y, where);
    }

    public String terrainName(String name, int x, int y, CoordCheckerInterface where) {
        if (!where.where(x, y + 1)) name += "N";
        if (!where.where(x, y - 1)) name += "S";
        if (!where.where(x + 1, y)) name += "E";
        if (!where.where(x - 1, y)) name += "W";
        if (where.where(x + 1, y) && where.where(x, y + 1) && !where.where(x + 1, y + 1)) name += "NEC";
        if (where.where(x + 1, y) && where.where(x, y - 1) && !where.where(x + 1, y - 1)) name += "SEC";
        if (where.where(x - 1, y) && where.where(x, y - 1) && !where.where(x - 1, y - 1)) name += "SWC";
        if (where.where(x - 1, y) && where.where(x, y + 1) && !where.where(x - 1, y + 1)) name += "NWC";
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
