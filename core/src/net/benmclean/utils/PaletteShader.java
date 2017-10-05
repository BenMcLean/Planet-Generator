package net.benmclean.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PaletteShader implements Disposable {
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
            "varying LOWP vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texPalette;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "   float color = texture2D(u_texture, v_texCoords).r;\n" + // on separate line for GWT
            "	gl_FragColor = texture2D(u_texPalette, vec2(color, 0)).rgba * v_color.rgba;\n" +
            "}";

    public static final String fragmentShaderYieldTransparency = "#ifdef GL_ES\n" +
            "#define LOWP lowp\n" +
            "precision mediump float;\n" +
            "#else\n" +
            "#define LOWP\n" +
            "#endif\n" +
            "varying LOWP vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texPalette;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "   vec2 color = texture2D(u_texture, v_texCoords).ra;" +
            "	gl_FragColor = vec4(\n" +
            "       texture2D(u_texPalette, vec2(color.r, 0)).rgb * v_color.rgb, \n" +
            "       color.y * v_color.a\n" +
            "   );\n" +
            "}";

    protected Palette4 palette;
    protected Texture texture;
    protected ShaderProgram shader;

    public PaletteShader(Palette4 palette) {
        this.palette = palette;
        texture = palette.makeTexture();
    }

    public PaletteShader bind(ShaderProgram shader) {
        return bind(this, shader);
    }

    public PaletteShader bind(ShaderProgram shader, int unit) {
        return bind(this, shader, unit);
    }

    public static PaletteShader bind(PaletteShader paletteShader, ShaderProgram shader) {
        return bind(paletteShader, shader, 1);
    }

    public static PaletteShader bind(PaletteShader paletteShader, ShaderProgram shader, int unit) {
        paletteShader.texture.bind(unit);
        shader.setUniformi("u_texPalette", unit);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0); // reset to texture 0 for SpriteBatch
        return paletteShader;
    }

    public static ShaderProgram makeShader() {
        return makeShader(PaletteShader.fragmentShaderYieldTransparency);
    }

    public static ShaderProgram makeShader(String fragment) {
        return makeShader(PaletteShader.vertexShader, fragment);
    }

    public static ShaderProgram makeShader(String vertex, String fragment) {
        ShaderProgram shader = new ShaderProgram(vertex, fragment);
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        return shader;
    }

    @Override
    public void dispose() {
        if (palette != null) palette.dispose();
        if (shader != null) shader.dispose();
        if (texture != null) texture.dispose();
    }
}
