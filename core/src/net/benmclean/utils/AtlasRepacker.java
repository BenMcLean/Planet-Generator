package net.benmclean.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

/**
 * AtlasRepacker makes it possible to palette-swap the game's art assets at runtime.
 * Using the instance versions of the methods will also preserve 9-patch info, as will repackAtlas.
 * Static versions of the pack method are also available for use with PixmapPacker, but these will not preserve 9-patch info because while AtlasRegion has 9-patch support, PixmapPacker sadly does not as of this writing.
 * It is recommended that when mixing procedurally generated images with recolored images from a TextureAtlas, the procedurally generated images coming in as pixmaps should have their names prefixed as "procgen/" or get some other name which is guaranteed to not be in the atlas to avoid incorrect 9-patch information being copied over.
 *
 * @author BenMcLean
 */
public class AtlasRepacker implements Disposable {
    public static final int transparent = Color.rgba8888(0f, 0f, 0f, 0f);
    protected PixmapPacker packer;
    protected TextureAtlas atlas;

    public AtlasRepacker(TextureAtlas atlas) {
        packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false);
        this.atlas = atlas;
    }

    public AtlasRepacker pack(String category) {
        pack(category, atlas, packer);
        return this;
    }

    public AtlasRepacker pack(String category, Palette4 palette) {
        pack(category, atlas, palette, packer);
        return this;
    }

    /**
     * This method should preserve 9-patch info.
     */
    public TextureAtlas generateTextureAtlas() {
        TextureAtlas textureAtlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);

        // Copying 9-patch info from the old atlas into the new atlas
        for (TextureAtlas.AtlasRegion region : textureAtlas.getRegions()) {
            TextureAtlas.AtlasRegion raw = atlas.findRegion(region.name);
            if (raw != null) {
                if (raw.pads != null) {
                    region.pads = new int[raw.pads.length];
                    System.arraycopy(raw.pads, 0, region.pads, 0, raw.pads.length);
                }
                if (raw.splits != null) {
                    region.splits = new int[raw.splits.length];
                    System.arraycopy(raw.splits, 0, region.splits, 0, raw.splits.length);
                }
            }
        }
        return textureAtlas;
    }

    /**
     * This method should preserve 9-patch info.
     */
    public static TextureAtlas repackAtlas(TextureAtlas atlas, Palette4 palette) {
        AtlasRepacker repacker = new AtlasRepacker(atlas).pack("", palette);
        TextureAtlas result = repacker.generateTextureAtlas();
        repacker.dispose();
        return result;
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas raw, PixmapPacker packer) {
        pack("", raw, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(String category, TextureAtlas raw, PixmapPacker packer) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(region, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas.AtlasRegion region, PixmapPacker packer) {
        pack(region, null, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas raw, Palette4 palette, PixmapPacker packer) {
        pack("", raw, palette, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(String category, TextureAtlas raw, Palette4 palette, PixmapPacker packer) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(region, palette, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas.AtlasRegion region, Palette4 palette, PixmapPacker packer) {
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap result = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
        Color color = new Color();
        for (int x = 0; x < region.getRegionWidth(); x++)
            for (int y = 0; y < region.getRegionHeight(); y++) {
                color.set(pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));
                if (palette == null)
                    result.drawPixel(x, y, pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));
                else if (color.a > .05)
                    result.drawPixel(x, y, Color.rgba8888(palette.get((int) (color.r * 3.9999))));
                else
                    result.drawPixel(x, y, transparent);
            }
        packer.pack(region.toString(), result);
        pixmap.dispose();
    }

    public AtlasRepacker pack(String name, Texture texture) {
        pack(name, texture, packer);
        return this;
    }

    public static void pack(String name, Texture texture, PixmapPacker packer) {
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        packer.pack(name, texture.getTextureData().consumePixmap());
    }

    public AtlasRepacker pack(String name, Texture texture, Palette4 palette) {
        pack(name, texture, palette, packer);
        return this;
    }

    public AtlasRepacker pack(String name, Pixmap pixmap, Palette4 palette) {
        pack(name, pixmap, palette, packer);
        return this;
    }

    public static void pack(String name, Pixmap pixmap, Palette4 palette, PixmapPacker packer) {
        packer.pack(name, recolor(pixmap, palette));
    }

    public static void pack(String name, Texture texture, Palette4 palette, PixmapPacker packer) {
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        packer.pack(name, recolor(texture.getTextureData().consumePixmap(), palette));
    }

    public static Pixmap recolor(Pixmap pixmap, Palette4 palette) {
        Pixmap result = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        Color color = new Color();
        for (int x = 0; x < pixmap.getWidth(); x++)
            for (int y = 0; y < pixmap.getHeight(); y++) {
                color.set(pixmap.getPixel(x, y));
                if (palette == null)
                    result.drawPixel(x, y, pixmap.getPixel(x, y));
                else if (color.a > .05)
                    result.drawPixel(x, y, Color.rgba8888(palette.get((int) (color.r * 3.9999))));
                else
                    result.drawPixel(x, y, transparent);
            }
        return result;
    }

    /**
     * Does not dispose atlas!
     */
    @Override
    public void dispose() {
        packer.dispose();
    }
}
