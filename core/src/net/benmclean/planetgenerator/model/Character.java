package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.Palette4;
import squidpony.squidmath.RNG;
import squidpony.squidmath.ThrustRNG;

public class Character {
    protected Assets assets;
    public TextureAtlas atlas = new TextureAtlas();
    public Assets.Character character;

    public Character(Character other) {
        this.assets = other.assets;
        this.atlas = other.atlas;
        this.character = other.character;
    }

    public Character(long SEED, Assets assets) {
        this(Character.random(SEED, assets));
    }

    public static Character random(long SEED, Assets assets) {
        return new Character(
                Assets.Character.values()[new RNG(new ThrustRNG(SEED)).nextInt(Assets.Character.values().length)],
                Palette4.grey(),
                assets);
    }

    public Character(Assets.Character character, Palette4 palette, Assets assets) {
        this.assets = assets;
        this.character = character;
        AtlasRepacker repacker = new AtlasRepacker(assets.atlas)
                .pack("utils")
                .pack(characterPrefix());
        atlas = repacker.generateTextureAtlas();
        repacker.dispose();
    }

    public String characterPrefix() {
        return "characters/" + character;
    }

    public TextureRegion findRegion(String suffix) {
        return atlas.findRegion(characterPrefix() + suffix);
    }
}
