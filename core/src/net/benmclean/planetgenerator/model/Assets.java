package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    public static final int transparent = Color.rgba8888(0f, 0f, 0f, 0f);
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    //    public ShaderProgram shader;
    public Texture one;
    public TextureAtlas atlas;
//    public Skin commodore64;

    public Assets() {
        // vertexShader copied from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g2d/SpriteBatch.java#L132
        // fragmentShader is where the magic happens
//        shader = new ShaderProgram(PaletteShader.vertexShader, PaletteShader.fragmentShaderYieldTransparency);
//        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
//        shader.begin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.drawPixel(0, 0, -1);
        one = new Texture(pixmap);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();

//        commodore64 = new Skin(Gdx.files.internal("commodore64/uiskin.json"));

        atlas = new TextureAtlas("art.atlas");
    }

    public void dispose() {
        one.dispose();
        atlas.dispose();
    }
}
