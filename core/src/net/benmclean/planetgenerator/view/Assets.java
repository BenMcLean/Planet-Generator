package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    public ShaderProgram shader;
    public Texture one;
    public TextureAtlas atlas;
    public Skin commodore64;

    public Assets () {
        // vertexShader copied from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g2d/SpriteBatch.java#L132
        // fragmentShader is where the magic happens
        shader = new ShaderProgram(
                "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                        + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                        + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                        + "uniform mat4 u_projTrans;\n" //
                        + "varying vec4 v_color;\n" //
                        + "varying vec2 v_texCoords;\n" //
                        + "\n" //
                        + "void main()\n" //
                        + "{\n" //
                        + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                        + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                        + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                        + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                        + "}\n"
                ,
                "#ifdef GL_ES\n" +
                        "#define LOWP lowp\n" +
                        "precision mediump float;\n" +
                        "#else\n" +
                        "#define LOWP\n" +
                        "#endif\n" +
                        "varying vec2 v_texCoords;\n" +
                        "uniform sampler2D u_texPalette;\n" +
                        "uniform sampler2D u_texture;\n\n" +
                        "void main() {\n" +
                        "   vec4 color = texture2D(u_texture, v_texCoords).rgba;\n" + // on separate line for GWT
                        "	gl_FragColor = texture2D(u_texPalette, vec2(color.r, 0)).rgba;\n" +
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
        floor = atlas.findRegion("terrain/GrassWater");

        commodore64 = new Skin(new FileHandle("commodore64/uiskin.json"));
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

    public static abstract class CoordCheckerInterface {
        public abstract boolean where (int x, int y);
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
    }
}
