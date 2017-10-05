package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.benmclean.utils.PaletteShader;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
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

        packIn("../assets-raw", "utils", packer);
        packIn("../assets-raw", "characters", packer);
        packIn("../assets-raw", "terrain", packer);

        TextureAtlas atlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
        packer.dispose();
        return atlas;
    }

    public static void packIn(String path, String category, PixmapPacker packer) {
        FileHandle[] files = Gdx.files.internal(path + "/" + category).list();
        for (FileHandle file : files)
            if (file.extension().equalsIgnoreCase("png"))
                packer.pack(category + "/" + file.nameWithoutExtension(), new Pixmap(file));
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
