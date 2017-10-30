package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.GenSprite;
import net.benmclean.utils.Palette4;

/**
 * Created by Benjamin on 11/19/2016.
 */
public class Assets {
    public static final int transparent = Color.rgba8888(0f, 0f, 0f, 0f);
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    public Texture one;
    public TextureAtlas atlas;
    public Skin skin;

    public Assets() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.drawPixel(0, 0, -1);
        one = new Texture(pixmap);
        one.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();

        skin = new Skin(
                Gdx.files.internal("DOS/uiskin.json"),
                AtlasRepacker.repackAtlas(
                        new TextureAtlas(Gdx.files.internal("DOS/uiskin.atlas")),
                        Palette4.greenUI()
                )
        );

        atlas = new TextureAtlas("art.atlas");
    }

    public static Pixmap ship(long SEED) {
        return GenSprite.generatePixmap(
                new GenSprite.Mask(new int[]{
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 1, 1,
                        0, 0, 0, 0, 1, -1,
                        0, 0, 0, 1, 1, -1,
                        0, 0, 0, 1, 1, -1,
                        0, 0, 1, 1, 1, -1,
                        0, 1, 1, 1, 2, 2,
                        0, 1, 1, 1, 2, 2,
                        0, 1, 1, 1, 2, 2,
                        0, 1, 1, 1, 1, -1,
                        0, 0, 0, 1, 1, 1,
                        0, 0, 0, 0, 0, 0
                }, 6, 12, true, false),
                true, 0.3, 0.2, 0.3, 0.5, SEED);
    }

    public void dispose() {
        one.dispose();
        atlas.dispose();
    }

    public enum Terrain {
        Sand("Sand"),
        Snow("Snow"),
        Grass("Grass");
        private String string;
        Terrain(String string) {
            this.string = string;
        }
        public String toString() {
            return string;
        }
    }
    public enum Biome {
        Bump0("Bump0"),
        Bump1("Bump1"),
        Dune0("Dune0"),
        Dune1("Dune1"),
        Hill0("Hill0"),
        Hill1("Hill1"),
        Oak0("Oak0"),
        Oak1("Oak1"),
        Oval0("Oval0"),
        Oval1("Oval1"),
        Palm0("Palm0"),
        Palm1("Palm1"),
        Pine0("Pine0"),
        Pine1("Pine1"),
        Tri0("Tri0"),
        Tri1("Tri1");
        private String string;
        Biome(String string) {
            this.string = string;
        }
        public String toString() {
            return string;
        }
    }
    public enum Character {
        Astronaut("Astronaut"),
        Blob("Blob"),
        Boy("Boy"),
        Chinese("Chinese"),
        Girl("Girl"),
        Knight("Knight"),
        Man("Man"),
        Skeleton("Skeleton"),
        Snake("Snake"),
        Spectre("Spectre"),
        Woman("Woman");
        private String string;
        Character(String string) {
            this.string = string;
        }
        public String toString() {
            return string;
        }
    }
}
