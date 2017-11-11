package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.Palette4;
import net.benmclean.utils.PaletteShader;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.LightRNG;
import squidpony.squidmath.RNG;

public class Character implements Disposable {
    protected Assets assets;
    public TextureAtlas atlas = new TextureAtlas();
    public Assets.Character character;
    public Palette4 palette;

    public Character(Character other) {
        this.assets = other.assets;
        this.atlas = other.atlas;
        this.character = other.character;
        this.palette = other.palette;
    }

    public Character(long SEED, Assets assets) {
        this(Character.random(SEED, assets));
    }

    public static Character random(long SEED, Assets assets) {
        return new Character(
                Assets.Character.values()[LightRNG.determineBounded(SEED, Assets.Character.values().length)],
                Palette4.fade(SColor.randomColorWheel(new RNG(SEED), 2, 2)),
                assets);
    }

    public Character(Assets.Character character, Palette4 palette, Assets assets) {
        this.assets = assets;
        this.character = character;
        this.palette = palette;
        PaletteShader shader = new PaletteShader(palette, assets.shader);
        AtlasRepacker repacker = new AtlasRepacker(assets.atlas)
                .pack("utils", assets.grey)
                .pack(characterPrefix(), shader);
        shader.dispose();
        atlas = repacker.generateTextureAtlas();
        repacker.dispose();
    }

    public String characterPrefix() {
        return "characters/" + character;
    }

    public TextureRegion findRegion(String suffix) {
        return atlas.findRegion(characterPrefix() + suffix);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
