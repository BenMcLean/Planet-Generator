package net.benmclean.planetgenerator.model;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.benmclean.utils.AtlasRepacker;
import net.benmclean.utils.Palette4;

public class Character {
    protected Assets assets;
    public TextureAtlas atlas = new TextureAtlas();
    public String character;

    public Character(long SEED, Assets assets) {
        this("Astronaut", Palette4.grey(), assets);
    }

    public Character(String character, Palette4 palette, Assets assets) {
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
