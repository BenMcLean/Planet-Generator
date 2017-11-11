package net.benmclean.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    protected Texture texture;
    protected ShaderProgram shader;

    public PaletteShader(Palette4 palette, ShaderProgram shader) {
        this(palette.texture(), shader);
    }

    public PaletteShader(Texture texture, ShaderProgram shader) {
        this.texture = texture;
        this.shader = shader;
    }

    public PaletteShader bind() {
        return bind(this, shader);
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

    public ShaderProgram getShader() {
        return shader;
    }

    /**
     * Does not dispose shader!
     */
    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
        if (batch != null) batch.dispose();
        if (buffer != null) buffer.dispose();
    }

    protected FrameBuffer buffer;
    protected SpriteBatch batch;
    protected Viewport view;

    public Pixmap recolor(TextureRegion region) {
        region.flip(false, true);
        if (batch == null) batch = new SpriteBatch();
        if (buffer == null || buffer.getWidth() != region.getRegionWidth() || buffer.getHeight() != region.getRegionHeight()) {
            if (buffer != null) buffer.dispose();
            buffer = new FrameBuffer(Pixmap.Format.RGBA8888, region.getRegionWidth(), region.getRegionHeight(), false, false);
        }
        if (view == null || view.getScreenWidth() != region.getRegionWidth() || view.getScreenHeight() != region.getRegionHeight())
            view = new FitViewport(region.getRegionWidth(), region.getRegionHeight());
        view.getCamera().position.set(region.getRegionWidth() / 2, region.getRegionWidth() / 2, 0);
        view.update(region.getRegionWidth(), region.getRegionHeight());
        buffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(view.getCamera().combined);
        batch.begin();
        if (shader != null) {
            batch.setShader(shader);
            bind();
        }
        batch.draw(region, 0, 0);
        batch.end();
        Pixmap result = ScreenUtils.getFrameBufferPixmap(0, 0, region.getRegionWidth(), region.getRegionHeight());
        buffer.end();
        region.flip(false, true);
        return result;
    }
}
