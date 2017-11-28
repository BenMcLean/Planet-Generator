package net.benmclean.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * AtlasRepacker makes it possible to palette-swap the game's art assets at runtime.
 * This works together with the static pack methods in PaletteShader. The advantage of using this class as a wrapper for those methods are twofold:
 * 1. Cleaner syntax.
 * 2. When repacking a TextureAtlas.AtlasRegion, this class ensures that 9-patch data  (pads and splits) is preserved.
 *
 * @author BenMcLean
 */
public class AtlasRepacker implements Disposable {
    protected PixmapPacker packer;
    public Map<String, int[]> pads = new HashMap<String, int[]>();
    public Map<String, int[]> splits = new HashMap<String, int[]>();

    public AtlasRepacker() {
        this(new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false));
    }

    /**
     * @param packer For custom PixmapPacker settings.
     */
    public AtlasRepacker(PixmapPacker packer) {
        this.packer = packer;
    }

    public TextureAtlas generateTextureAtlas() {
        TextureAtlas textureAtlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);

        // Copying 9-patch info from the old atlas into the new atlas
        for (TextureAtlas.AtlasRegion region : textureAtlas.getRegions()) {
            if (pads.containsKey(region.name)) region.pads = pads.get(region.name);
            if (splits.containsKey(region.name)) region.splits = splits.get(region.name);
        }
        return textureAtlas;
    }

    public static TextureAtlas repackAtlas(TextureAtlas atlas, PaletteShader palette) {
        AtlasRepacker repacker = new AtlasRepacker().pack(atlas, palette);
        TextureAtlas result = repacker.generateTextureAtlas();
        repacker.dispose();
        return result;
    }

    public static TextureAtlas repackAtlas(TextureAtlas atlas, Color[] palette) {
        AtlasRepacker repacker = new AtlasRepacker().pack(atlas, palette);
        TextureAtlas result = repacker.generateTextureAtlas();
        repacker.dispose();
        return result;
    }

    public AtlasRepacker pack(TextureAtlas atlas, PaletteShader palette) {
        return pack("", atlas, palette);
    }

    public AtlasRepacker pack(TextureAtlas atlas, Color[] palette) {
        return pack("", atlas, palette);
    }

    public AtlasRepacker pack(String category, TextureAtlas raw, PaletteShader palette) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(region, palette);
        return this;
    }

    public AtlasRepacker pack(String category, TextureAtlas raw, Color[] palette) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(region, palette);
        return this;
    }

    public AtlasRepacker pack(TextureAtlas.AtlasRegion region, Color[] palette) {
        return pack(region.name, region, palette, region.pads, region.splits);
    }

    public AtlasRepacker pack(TextureAtlas.AtlasRegion region, PaletteShader palette) {
        return pack(region.name, region, palette, region.pads, region.splits);
    }

    public AtlasRepacker pack(TextureAtlas.AtlasRegion region, Color[] palette, int[] pads, int[] splits) {
        return pack(region.name, region, palette, pads, splits);
    }

    public AtlasRepacker pack(TextureAtlas.AtlasRegion region, PaletteShader palette, int[] pads, int[] splits) {
        return pack(region.name, region, palette, pads, splits);
    }

    public AtlasRepacker pack(String name, TextureAtlas.AtlasRegion region, Color[] palette) {
        return pack(name, region, palette, region.pads, region.splits);
    }

    public AtlasRepacker pack(String name, TextureAtlas.AtlasRegion region, PaletteShader palette) {
        return pack(name, region, palette, region.pads, region.splits);
    }

    public AtlasRepacker pack(String name, TextureAtlas.AtlasRegion region, Color[] palette, int[] pads, int[] splits) {
        PaletteShader.pack(packer, name, region, palette);
        copyNinePatch(name, pads, splits);
        return this;
    }

    public AtlasRepacker pack(String name, TextureAtlas.AtlasRegion region, PaletteShader palette, int[] pads, int[] splits) {
        palette.pack(packer, name, region);
        copyNinePatch(name, pads, splits);
        return this;
    }

    public AtlasRepacker pack(String name, Texture texture) {
        PaletteShader.pack(packer, name, texture);
        return this;
    }

    public AtlasRepacker pack(String name, Texture texture, int[] pads, int[] splits) {
        copyNinePatch(name, pads, splits);
        return pack(name, texture);
    }

    public AtlasRepacker pack(String name, Texture texture, Color[] palette) {
        PaletteShader.pack(packer, name, texture, palette);
        return this;
    }

    public AtlasRepacker pack(String name, Pixmap pixmap) {
        packer.pack(name, pixmap);
        return this;
    }

    public AtlasRepacker pack(String name, Pixmap pixmap, int[] pads, int[] splits) {
        copyNinePatch(name, pads, splits);
        return pack(name, pixmap);
    }

    public AtlasRepacker pack(String name, Pixmap pixmap, Color[] palette) {
        return pack(name, pixmap, palette, null, null);
    }

    public AtlasRepacker pack(String name, Pixmap pixmap, Color[] palette, int[] pads, int[] splits) {
        PaletteShader.pack(packer, name, pixmap, palette);
        copyNinePatch(name, pads, splits);
        return this;
    }

    /**
     * A copy of the 9-patch data is made to ensure the new atlas doesn't change in response to changes in the old atlas.
     */
    public void copyNinePatch(String name, int[] pads, int[] splits) {
        if (pads != null) {
            int[] newPads = new int[pads.length];
            System.arraycopy(pads, 0, newPads, 0, pads.length);
            this.pads.put(name, newPads);
        }
        if (splits != null) {
            int[] newSplits = new int[splits.length];
            System.arraycopy(splits, 0, newSplits, 0, splits.length);
            this.splits.put(name, newSplits);
        }
    }

    @Override
    public void dispose() {
        packer.dispose();
    }
}
