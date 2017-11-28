package net.benmclean.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PaletteShader implements Disposable {
    public static final int transparent = Color.rgba8888(0f, 0f, 0f, 0f);

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

    public PaletteShader(Color[] colors, ShaderProgram shader) {
        this(texture(colors), shader);
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

    /**
     * This is the old and (possibly slower) way to recolor without using a shader
     */
    public static Pixmap recolor(Pixmap pixmap, Color[] palette) {
        Pixmap result = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        Color color = new Color();
        float length = palette.length - .0001f;
        for (int x = 0; x < pixmap.getWidth(); x++)
            for (int y = 0; y < pixmap.getHeight(); y++) {
                color.set(pixmap.getPixel(x, y));
                result.drawPixel(x, y,
                        color.a > .05f ?
                                Color.rgba8888(palette[(int) (color.r * length)])
                                :
                                transparent
                );
            }
        return result;
    }

    /**
     * This is the old and (possibly slower) way to recolor without using a shader
     */
    public static Pixmap recolor(TextureAtlas.AtlasRegion region, Color[] palette) {
        Texture texture = region.getTexture();
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap result = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
        Color color = new Color();
        float length = palette.length - .0001f;
        for (int x = 0; x < region.getRegionWidth(); x++)
            for (int y = 0; y < region.getRegionHeight(); y++) {
                color.set(pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));
                result.drawPixel(x, y,
                        color.a > .05f ?
                                Color.rgba8888(palette[(int) (color.r * length)])
                                :
                                transparent
                );
            }
        return result;
    }

    public static Pixmap pixmap(Color[] colors) {
        Pixmap pixmap = new Pixmap(colors.length, 1, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < colors.length; x++)
            pixmap.drawPixel(x, 0, Color.rgba8888(colors[x]));
        return pixmap;
    }

    public static Texture texture(Color[] colors) {
        Pixmap pixmap = pixmap(colors);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void pack(PixmapPacker packer, TextureAtlas raw) {
        pack(packer, "", raw, this);
    }

    public static void pack(PixmapPacker packer, TextureAtlas raw, PaletteShader palette) {
        pack(packer, "", raw, palette);
    }

    public void pack(PixmapPacker packer, String category, TextureAtlas raw) {
        pack(packer, category, raw, this);
    }

    public static void pack(PixmapPacker packer, String category, TextureAtlas raw, PaletteShader palette) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(packer, region, palette);
    }

    public void pack(PixmapPacker packer, TextureAtlas.AtlasRegion region) {
        pack(packer, region, this);
    }

    public static void pack(PixmapPacker packer, TextureAtlas.AtlasRegion region, PaletteShader palette) {
        pack(packer, region.name, region, palette);
    }

    public void pack(PixmapPacker packer, String newName, TextureAtlas.AtlasRegion region) {
        pack(packer, newName, region, this);
    }

    public static void pack(PixmapPacker packer, String newName, TextureAtlas.AtlasRegion region, PaletteShader palette) {
        packer.pack(newName, palette.recolor(region));
    }

    public static void pack(PixmapPacker packer, TextureAtlas raw, Color[] palette) {
        pack(packer, "", raw, palette);
    }

    public static void pack(PixmapPacker packer, String category, TextureAtlas raw, Color[] palette) {
        for (TextureAtlas.AtlasRegion region : raw.getRegions())
            if (region.name.startsWith(category))
                pack(packer, region, palette);
    }

    public static void pack(PixmapPacker packer, TextureAtlas.AtlasRegion region, Color[] palette) {
        pack(packer, region.toString(), region, palette);
    }

    public static void pack(PixmapPacker packer, String newName, TextureAtlas.AtlasRegion region, Color[] palette) {
        packer.pack(newName, PaletteShader.recolor(region, palette));
    }

    public static void pack(PixmapPacker packer, String name, Pixmap pixmap, Color[] palette) {
        packer.pack(name, PaletteShader.recolor(pixmap, palette));
    }

    public static void pack(PixmapPacker packer, String name, Texture texture, Color[] palette) {
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        packer.pack(name, PaletteShader.recolor(texture.getTextureData().consumePixmap(), palette));
    }

    public static void pack(PixmapPacker packer, String name, Texture texture) {
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        packer.pack(name, texture.getTextureData().consumePixmap());
    }
}
