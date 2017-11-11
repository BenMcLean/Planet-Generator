package net.benmclean.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Benjamin on 12/26/2016.
 */
public class Palette4 implements Disposable {
    public static final int transparent = Color.rgba8888(0f, 0f, 0f, 0f);
    public final int size = 4;
    protected Color[] palette;

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
        set(
                r0, g0, b0, a0,
                r1, g1, b1, a1,
                r2, g2, b2, a2,
                r3, g3, b3, a3
        );
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
        return set(r0 / 255f, g0 / 255f, b0 / 255f, a0 / 255f,
                r1 / 255f, g1 / 255f, b1 / 255f, a1 / 255f,
                r2 / 255f, g2 / 255f, b2 / 255f, a2 / 255f,
                r3 / 255f, g3 / 255f, b3 / 255f, a3 / 255f);
    }

    public Palette4 set(
            float r0, float g0, float b0, float a0,
            float r1, float g1, float b1, float a1,
            float r2, float g2, float b2, float a2,
            float r3, float g3, float b3, float a3) {
        return set(new Color(r0, g0, b0, a0),
                new Color(r1, g1, b1, a1),
                new Color(r2, g2, b2, a2),
                new Color(r3, g3, b3, a3));
    }

    public Palette4 set(Color zero, Color one, Color two, Color three) {
        palette = new Color[4];
        palette[0] = zero;
        palette[1] = one;
        palette[2] = two;
        palette[3] = three;
        return this;
    }

    public Palette4(Color[] color) {
        this(color[0], color[1], color[2], color[3]);
    }

    public Palette4(Color color0, Color color1, Color color2, Color color3) {
        set(color0, color1, color2, color3);
    }

    public Color[] get() {
        return palette;
    }

    public Color get(int x) {
        return palette[x];
    }

    public Pixmap pixmap() {
        Pixmap pixmap = new Pixmap(size, 1, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < size; x++) {
            pixmap.drawPixel(x, 0, Color.rgba8888(palette[x]));
        }
        return pixmap;
    }

    public Texture texture() {
        Pixmap pixmap = pixmap();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void dispose() {
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
                0, 0, 0, 255, // black
                0, 119, 255, 255, // dark blue
                0, 168, 0, 255, // dark green
                159, 227, 14, 255 // green
        );
        // water color is 15, 215, 255, 255,
    }

    public static Palette4 blueUI() {
        return new Palette4(
                0, 0, 0, 255,
                0, 0, 127, 255,
                0, 0, 255, 255,
                170, 170, 255, 255
        );
    }

    public static Palette4 greenUI() {
        return new Palette4(
                0, 0, 0, 255,
                0, 62, 0, 255,
                0, 127, 0, 255,
                0, 255, 0, 255
        );
    }

    public static Palette4 fade(Color color) {
        return new Palette4(
                Color.BLACK,
                new Color(color.r / 2f, color.g / 2f, color.b / 2f, 1f),
                color,
                new Color(color.r * 1.5f, color.g * 1.5f, color.b * 1.5f, 1f)
        );
    }
}
