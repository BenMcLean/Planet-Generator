package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Benjamin on 12/26/2016.
 */
public class Palette4 implements Disposable {
    protected Pixmap pixmap;
    protected Texture texture;

    protected void makePixmap() {
        pixmap = new Pixmap(4, 1, Pixmap.Format.RGBA8888);
    }

    public Palette4(float[][] x) {
        this(x[0], x[1], x[2], x[3]);
    }

    public Palette4(float[] zero, float[] one, float[] two, float[] three) {
        this(
                zero[0], zero[1], zero[2], zero[3],
                one[0], one[1], one[2], one[3],
                two[0], two[1], two[2], two[3],
                three[0], three[1], three[2], three[3]
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

    public Palette4(int[][] x) {
        this(x[0], x[1], x[2], x[3]);
    }

    public Palette4(int[] zero, int[] one, int[] two, int[] three) {
        this(zero[0], zero[1], zero[2], zero[3],
                one[0], one[1], one[2], one[3],
                two[0], two[1], two[2], two[3],
                three[0], three[1], three[2], three[3]);
    }

    public Palette4(
            int r0, int g0, int b0, int a0,
            int r1, int g1, int b1, int a1,
            int r2, int g2, int b2, int a2,
            int r3, int g3, int b3, int a3) {
        makePixmap();
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

    public Palette4(Color[] x) {
        this(x[0], x[1], x[2], x[3]);
    }

    public Palette4(Color color0, Color color1, Color color2, Color color3) {
        makePixmap();
        set(color0, color1, color2, color3);
    }

    public Palette4 set(Color color0, Color color1, Color color2, Color color3) {
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
}
