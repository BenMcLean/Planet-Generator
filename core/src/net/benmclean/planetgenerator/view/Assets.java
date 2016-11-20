package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    public Texture one;

    public Assets () {
        Pixmap pixmap1 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap1.drawPixel(0, 0, -1);
        one = new Texture(pixmap1);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap1.dispose();
    }

    public Texture floor;
    public Texture wall;
    public void tempStuff() {
        Pixmap pixmap1 = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap1.drawRectangle(0, 0, 16, 16, Gdx.graphics.);
    }

    public void dispose() {
        one.dispose();
    }
}
