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

    public AtlasRepacker pack(String category, PaletteShader palette) {
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
    public static TextureAtlas repackAtlas(TextureAtlas atlas, PaletteShader palette) {
        AtlasRepacker repacker = new AtlasRepacker(atlas).pack("", palette);
        TextureAtlas result = repacker.generateTextureAtlas();
        repacker.dispose();
        return result;
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas raw, PaletteShader palette, PixmapPacker packer) {
        pack("", raw, palette, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(String category, TextureAtlas raw, PaletteShader palette, PixmapPacker packer) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(region, palette, packer);
    }

    /**
     * This method does not copy 9-Patch info by itself!
     */
    public static void pack(TextureAtlas.AtlasRegion region, PaletteShader palette, PixmapPacker packer) {
        packer.pack(region.toString(), palette.recolor(region));
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

    public AtlasRepacker pack(String name, Pixmap pixmap) {
        packer.pack(name, pixmap);
        return this;
    }

    public AtlasRepacker pack(String name, Pixmap pixmap, Palette4 palette) {
        pack(name, pixmap, palette, packer);
        return this;
    }

    public static void pack(String name, Pixmap pixmap, Palette4 palette, PixmapPacker packer) {
        packer.pack(name, palette.recolor(pixmap));
    }

    public static void pack(String name, Texture texture, Palette4 palette, PixmapPacker packer) {
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        packer.pack(name, palette.recolor(texture.getTextureData().consumePixmap()));
    }

    /**
     * Does not dispose atlas!
     */
    @Override
    public void dispose() {
        packer.dispose();
    }
}
