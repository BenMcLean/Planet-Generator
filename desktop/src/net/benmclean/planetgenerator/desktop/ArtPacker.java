package net.benmclean.planetgenerator.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class ArtPacker {
    public static void main(final String[] args) throws Exception {
        TexturePacker.process("../assets-raw", ".", "art");
    }
}
