package net.benmclean.planetgenerator.view;

import com.strongjoshua.console.CommandExecutor;
import net.benmclean.planetgenerator.model.Universe;

public class Executor extends CommandExecutor {
    Universe universe;

    public Executor setUniverse(Universe universe) {
        this.universe = universe;
        return this;
    }

    public void say (String stuff) {
        console.log("Saying: " + stuff);
    }

    public void planet () {
        console.log("Loading new planet...");
        universe.nextPlanet();
        console.log("Planet " + universe.getPlanet().getSEED()  + " loaded.");
    }

    public void planet (long SEED) {
        console.log("Loading Planet " + SEED + "...");
        universe.nextPlanet(SEED);
        console.log("Planet " + SEED + " loaded.");
    }
}
