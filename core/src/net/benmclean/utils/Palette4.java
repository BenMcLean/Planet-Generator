package net.benmclean.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Benjamin on 12/26/2016.
 */
public class Palette4 implements Disposable {
	// vertexShader copied from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g2d/SpriteBatch.java#L132
    public static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
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
            + "}\n";

    public static final String fragmentShader = "#ifdef GL_ES\n" +
            "#define LOWP lowp\n" +
            "precision mediump float;\n" +
            "#else\n" +
            "#define LOWP\n" +
            "#endif\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texPalette;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "   vec4 color = texture2D(u_texture, v_texCoords).rgba;\n" + // on separate line for GWT
            "	gl_FragColor = texture2D(u_texPalette, vec2(color.r, 0)).rgba;\n" +
            "}";

    public static final String fragmentShaderYieldTransparency = "#ifdef GL_ES\n" +
            "#define LOWP lowp\n" +
            "precision mediump float;\n" +
            "#else\n" +
            "#define LOWP\n" +
            "#endif\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texPalette;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "   vec4 color = texture2D(u_texture, v_texCoords).rgba;\n" + // on separate line for GWT
            "	gl_FragColor = vec4(\n" +
            "       texture2D(u_texPalette, vec2(color.r, 0)).r, \n" +
            "       texture2D(u_texPalette, vec2(color.r, 0)).g, \n" +
            "       texture2D(u_texPalette, vec2(color.r, 0)).b, \n" +
            "       color.a\n" +
            "   );\n" +
            "}";

    protected Pixmap pixmap;
    protected Texture texture;

    protected Palette4 makePixmap() {
        if (pixmap != null) pixmap.dispose();
        pixmap = new Pixmap(4, 1, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        return this;
    }

    public Palette4(float[][] color) {
        this(color[0], color[1], color[2], color[3]);
    }

    public Palette4(float[] color0, float[] color1, float[] color2, float[] color3) {
        this(
                color0[0], color0[1], color0[2], color0[3],
                color1[0], color1[1], color1[2], color1[3],
                color2[0], color2[1], color2[2], color2[3],
                color3[0], color3[1], color3[2], color3[3]
        );
    }

    public Palette4(
            float r0, float g0, float b0, float a0,
            float r1, float g1, float b1, float a1,
            float r2, float g2, float b2, float a2,
            float r3, float g3, float b3, float a3) {
        makePixmap();
        set(
                r0, g0, b0, a0,
                r1, g1, b1, a1,
                r2, g2, b2, a2,
                r3, g3, b3, a3
        );
    }

    public Palette4 set(
            float r0, float g0, float b0, float a0,
            float r1, float g1, float b1, float a1,
            float r2, float g2, float b2, float a2,
            float r3, float g3, float b3, float a3) {
        makePixmap();
        pixmap.setColor(r0, g0, b0, a0);
        pixmap.drawPixel(0, 0);
        pixmap.setColor(r1, g1, b1, a1);
        pixmap.drawPixel(1, 0);
        pixmap.setColor(r2, g2, b2, a2);
        pixmap.drawPixel(2, 0);
        pixmap.setColor(r3, g3, b3, a3);
        pixmap.drawPixel(3, 0);
        return makeTexture();
    }

    public Palette4(int[][] color) {
        this(color[0], color[1], color[2], color[3]);
    }

    public Palette4(int[] color0, int[] color1, int[] color2, int[] color3) {
        this(
                color0[0], color0[1], color0[2], color0[3],
                color1[0], color1[1], color1[2], color1[3],
                color2[0], color2[1], color2[2], color2[3],
                color3[0], color3[1], color3[2], color3[3]
        );
    }

    public Palette4(
            int r0, int g0, int b0, int a0,
            int r1, int g1, int b1, int a1,
            int r2, int g2, int b2, int a2,
            int r3, int g3, int b3, int a3) {
        set(
                r0, g0, b0, a0,
                r1, g1, b1, a1,
                r2, g2, b2, a2,
                r3, g3, b3, a3
        );
    }

    public Palette4 set(
            int r0, int g0, int b0, int a0,
            int r1, int g1, int b1, int a1,
            int r2, int g2, int b2, int a2,
            int r3, int g3, int b3, int a3) {
        makePixmap();
        pixmap.setColor(r0 / 255f, g0 / 255f, b0 / 255f, a0 / 255f);
        pixmap.drawPixel(0, 0);
        pixmap.setColor(r1 / 255f, g1 / 255f, b1 / 255f, a1 / 255f);
        pixmap.drawPixel(1, 0);
        pixmap.setColor(r2 / 255f, g2 / 255f, b2 / 255f, a2 / 255f);
        pixmap.drawPixel(2, 0);
        pixmap.setColor(r3 / 255f, g3 / 255f, b3 / 255f, a3 / 255f);
        pixmap.drawPixel(3, 0);
        return makeTexture();
    }

    public Palette4(Color[] color) {
        this(color[0], color[1], color[2], color[3]);
    }

    public Palette4(Color color0, Color color1, Color color2, Color color3) {
        set(color0, color1, color2, color3);
    }

    public Palette4 set(Color color0, Color color1, Color color2, Color color3) {
        makePixmap();
        pixmap.setColor(color0);
        pixmap.drawPixel(0, 0);
        pixmap.setColor(color1);
        pixmap.drawPixel(1, 0);
        pixmap.setColor(color2);
        pixmap.drawPixel(2, 0);
        pixmap.setColor(color3);
        pixmap.drawPixel(3, 0);
        return makeTexture();
    }

    protected Palette4 makeTexture() {
        if (texture != null) texture.dispose();
        texture = new Texture(pixmap);
        return this;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getPixel(int x) {
        return pixmap.getPixel(x, 0);
    }

    @Override
    public void dispose() {
        pixmap.dispose();
        texture.dispose();
    }

    public static Palette4 gameboy() {
        return new Palette4(
                15, 56, 15, 255,
                48, 98, 48, 255,
                140, 173, 15, 255,
                156, 189, 15, 255
        );
    }

    public static Palette4 grey() {
        return new Palette4(
                0, 0, 0, 255,
                85, 85, 85, 255,
                170, 170, 170, 255,
                255, 255, 255, 255
        );
    }

    public static Palette4 earth() {
        return new Palette4(
                0, 0, 0, 255,
                0, 119, 255, 255,
                15, 215, 255, 255,
                159, 227, 14, 255
        );
    }

    public Palette4 bind(ShaderProgram shader) {
        return bind(this, shader);
    }

    public Palette4 bind(ShaderProgram shader, int unit) {
        return bind(this, shader, unit);
    }

    public static Palette4 bind(Palette4 palette4, ShaderProgram shader) {
        return bind(palette4, shader, 1);
    }

    public static Palette4 bind(Palette4 palette4, ShaderProgram shader, int unit) {
        palette4.getTexture().bind(unit);
        shader.setUniformi("u_texPalette", unit);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0); // reset to texture 0 for SpriteBatch
        return palette4;
    }
}
