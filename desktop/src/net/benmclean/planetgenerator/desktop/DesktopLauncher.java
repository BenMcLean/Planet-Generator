package net.benmclean.planetgenerator.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.benmclean.planetgenerator.PlanetGeneratorGame;

public class DesktopLauncher {
	public static void main (String[] args) {
    	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
   		config.width = 1280;
    	config.height = 720;
//    	config.addIcon("Tentacle-16.png", Files.FileType.Internal);
//    	config.addIcon("Tentacle-32.png", Files.FileType.Internal);
//    	config.addIcon("Tentacle-128.png", Files.FileType.Internal);
		config.title = "Planet-Generator";
    	new LwjglApplication(new PlanetGeneratorGame(), config);
    }
}
