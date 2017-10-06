package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.benmclean.utils.Palette4;
import net.benmclean.utils.PaletteShader;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    static final int transparent = Color.rgba8888(0f, 0f, 0f, 0f);
    public ShaderProgram shader;
    public Texture one;
    public TextureAtlas atlas;
    public Skin commodore64;

    public Assets() {
        // vertexShader copied from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g2d/SpriteBatch.java#L132
        // fragmentShader is where the magic happens
        shader = new ShaderProgram(PaletteShader.vertexShader, PaletteShader.fragmentShaderYieldTransparency);

        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        shader.begin();

        Pixmap pixmap1 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap1.drawPixel(0, 0, -1);
        one = new Texture(pixmap1);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap1.dispose();

        //atlas = new TextureAtlas("art.atlas");
        atlas = packTextureAtlas();

        commodore64 = new Skin(Gdx.files.internal("commodore64/uiskin.json"));
    }

    public static TextureAtlas packTextureAtlas() {
        PixmapPacker packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false);
        TextureAtlas raw = new TextureAtlas("art.atlas");

        packIn("utils", raw, packer);
        packIn("characters", raw, packer);
        Palette4 earth = Palette4.earth();
        packIn("terrain", raw, earth, packer);
        earth.dispose();
        raw.dispose();

        TextureAtlas atlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
        packer.dispose();
        return atlas;
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
                    result.drawPixel(x, y, transparent);
            }

        packer.pack(region.toString(), result);
        texture.dispose();
    }

    public static abstract class CoordCheckerInterface {
        public abstract boolean where(int x, int y);
    }

    String terrainName(String name, int x, int y, CoordCheckerInterface where) {
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

    public void dispose() {
        one.dispose();
        atlas.dispose();
    }
}
