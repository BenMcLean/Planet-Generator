package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.sudoplay.joise.mapping.Mapping;
import com.sudoplay.joise.mapping.MappingMode;
import com.sudoplay.joise.mapping.MappingRange;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import net.benmclean.utils.Palette4;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

public class Planet {
    public static final int SIZE_X = 128;
    public static final int SIZE_Y = 128;
    private long SEED;
    private RNG rng;
    private Assets assets;
    protected Coord playerCoord = Coord.get(SIZE_X / 2, SIZE_Y / 2);
    protected boolean[][] world;
    private TextureAtlas atlas;

    public TextureAtlas getAtlas () {
        return atlas;
    }

    public long getSEED () {
        return SEED;
    }

    public Planet(Planet planet) {
        this(planet.SEED, planet.assets);
    }

    public Planet(long SEED, Assets assets) {
        this.SEED = SEED;
        this.assets = assets;
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
    }

    public Boolean isWall(int x, int y) {
        //if (x < 0 || y < 0 || x > SIZE_X || y > SIZE_Y) return null;
        //if (x == 0 || y == 0 || x == SIZE_X - 1 || y == SIZE_Y - 1) return true;
        //return noise.eval(wrapX(x)/6f, wrapY(y)/6f, 0) < 0.25;
        //return bareDungeon[x][y] == '#';
        return world[wrapX(x)][wrapY(y)];
    }

    public TextureAtlas packTextureAtlas() {
        PixmapPacker packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false);
        packIn("utils", assets.atlas, packer);
        packIn("characters", assets.atlas, packer);
        Palette4 earth = Palette4.earth();
        packIn("terrain", assets.atlas, earth, packer);
        earth.dispose();
        return packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
    }

    public static void packIn(String category, TextureAtlas raw, PixmapPacker packer) {
        Array<TextureAtlas.AtlasRegion> regions = raw.getRegions();
        for (TextureAtlas.AtlasRegion region : regions)
            if (region.name.toString().startsWith(category))
                packIn(region, packer);
    }

    public static void packIn(TextureAtlas.AtlasRegion region, PixmapPacker packer) {
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();

        Pixmap result = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
        for (int x = 0; x < region.getRegionWidth(); x++)
            for (int y = 0; y < region.getRegionHeight(); y++)
                result.drawPixel(x, y, pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));

        packer.pack(region.toString(), result);
        texture.dispose();
    }

    public static void packIn(String category, TextureAtlas raw, Palette4 palette, PixmapPacker packer) {
        Array<TextureAtlas.AtlasRegion> regions = raw.getRegions();
        for (TextureAtlas.AtlasRegion region : regions)
            if (region.name.toString().startsWith(category))
                packIn(region, palette, packer);
    }

    public static void packIn(TextureAtlas.AtlasRegion region, Palette4 palette, PixmapPacker packer) {
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

    public static abstract class CoordCheckerInterface {
        public abstract boolean where(int x, int y);
    }

    public String terrainName (int x, int y, CoordCheckerInterface where) {
        return terrainName("terrain/GrassShore", x, y, where);
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
