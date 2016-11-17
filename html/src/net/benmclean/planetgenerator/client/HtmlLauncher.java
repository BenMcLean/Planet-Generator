package net.benmclean.planetgenerator.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.benmclean.planetgenerator.PlanetGeneratorGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(80 * 11, (24 + 8) * 22);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new PlanetGeneratorGame();
        }
}