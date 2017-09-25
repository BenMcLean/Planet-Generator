package net.benmclean.planetgenerator.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import net.benmclean.utils.Palette4;

public class ArtProcess {
    public static void main (String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new ArtProcessGame(), config);
    }

    public static class ArtProcessGame extends Game {
        @Override
        public void create() {

            Pixmap pixmap = new Pixmap(Gdx.files.internal("../assets-raw/characters/AstronautE0.png"));
            pixmap.setBlending(Pixmap.Blending.None);

            Palette4 palette = Palette4.gameboy();
            Color color = Color.WHITE;
            Color newColor = Color.WHITE;

            for (int x = 0; x < pixmap.getWidth(); x++) {
                for (int y = 0; y < pixmap.getHeight(); y++) {
                    color.set(pixmap.getPixel(x, y));
                    newColor.set(palette.getPixel((int) (color.r * 3.9999)));
                    pixmap.drawPixel(x, y, Color.rgba8888(newColor.r, newColor.g, newColor.b, color.a));
                }
            }

            FileHandle resultFile = Gdx.files.local("../assets-raw/utils/result.png");

            PixmapIO.writePNG(resultFile, pixmap);

            pixmap.dispose();

            System.out.println("Everything OK so far!");
            Gdx.app.exit();
        }
    }
}
