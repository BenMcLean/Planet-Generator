package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    public Texture one;
    private TextureAtlas atlas;

    public Assets () {
        Pixmap pixmap1 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap1.drawPixel(0, 0, -1);
        one = new Texture(pixmap1);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap1.dispose();

        atlas = new TextureAtlas("art.atlas");
        wall = atlas.findRegion("biomes/Tri1");
        floor = atlas.findRegion("terrain/GrassWater");
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
