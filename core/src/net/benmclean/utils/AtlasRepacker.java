package net.benmclean.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

/**
 * AtlasRepacker makes it possible to palette-swap the game's art assets at runtime.
 * Using the instance versions of the methods will also preserve 9-patch info.
 * Static versions are also available for use with PixmapPacker, but these will not preserve 9-patch info because while AtlasRegion has 9-patch support, PixmapPacker sadly does not as of this writing.
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
        pack(category, atlas, packer, palette);
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
            if (raw.pads != null) {
                region.pads = new int[raw.pads.length];
                System.arraycopy(raw.pads, 0, region.pads, 0, raw.pads.length);
            }
            if (raw.splits != null) {
                region.splits = new int[raw.splits.length];
                System.arraycopy(raw.splits, 0, region.splits, 0, raw.splits.length);
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

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas raw, PixmapPacker packer, Palette4 palette) {
        pack("", raw, packer, palette);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(String category, TextureAtlas raw, PixmapPacker packer, Palette4 palette) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(region, packer, palette);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas.AtlasRegion region, PixmapPacker packer, Palette4 palette) {
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

    /**
     * Does not dispose atlas!
     */
    @Override
    public void dispose() {
        packer.dispose();
    }
}
