package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    public ShaderProgram shader;
    public Texture one;
    public TextureAtlas atlas;

    public Assets () {
        shader = new ShaderProgram(
                "attribute vec4 a_position;\n" +
                        "attribute vec4 a_color;\n" +
                        "attribute vec2 a_texCoord0;\n" +
                        "uniform mat4 u_projTrans;\n" +
                        "varying vec4 v_color;\n" +
                        "varying vec2 v_texCoords;\n\n" +
                        "void main() {\n" +
                        "    v_color = a_color;\n" +
                        "    v_color.a = v_color.a * (256f / 255f);\n" +
                        "    v_texCoords = a_texCoord0;\n" +
                        "    gl_Position = u_projTrans * a_position;\n" +
                        "}"
                ,
                "#ifdef GL_ES\n" +
                        "#define LOWP lowp\n" +
                        "precision mediump float;\n" +
                        "#else\n" +
                        "#define LOWP\n" +
                        "#endif\n\n" +
                        "varying LOWP vec4 v_color;\n" +
                        "varying vec2 v_texCoords;\n" +
                        "uniform vec4 u_palette[4];\n" +
                        "uniform sampler2D u_texture;\n\n" +
                        "void main() {\n" +
                        "    gl_FragColor = u_palette[int(texture2D(u_texture, v_texCoords).r * 3.9999)];\n" +
                        "}"
        );
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        shader.begin();

        Pixmap pixmap1 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap1.drawPixel(0, 0, -1);
        one = new Texture(pixmap1);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap1.dispose();

        atlas = new TextureAtlas("art.atlas");
        wall = atlas.findRegion("biomes/Tri1");
        floor = atlas.findRegion("utils/color1");
    }

    public void applyPalette (Color[] palette) {
        int location = shader.getUniformLocation("u_palette[0]");
        for (int x = 0; x < palette.length; x++)
            shader.setUniformf(location + x, palette[x]);
    }

    public TextureAtlas.AtlasRegion wall;
    public TextureAtlas.AtlasRegion floor;
    public void tempStuff() {
        Pixmap pixmap1 = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap1.setColor(Color.BLUE);
        pixmap1.fill();
        Texture wallTesture = new Texture(pixmap1);
        wallTesture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        wall = new TextureAtlas.AtlasRegion(wallTesture, 0, 0, 16, 16);

        pixmap1.setColor(Color.GREEN);
        pixmap1.fill();
        Texture floorTesture = new Texture(pixmap1);
        floorTesture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        floor = new TextureAtlas.AtlasRegion(floorTesture, 0, 0, 16, 16);
    }

    public void dispose() {
        one.dispose();
    }
}
