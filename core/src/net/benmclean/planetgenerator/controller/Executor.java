package net.benmclean.planetgenerator.controller;

import com.strongjoshua.console.CommandExecutor;
import net.benmclean.planetgenerator.model.Universe;

public class Executor extends CommandExecutor {
    Universe universe;

    public Executor setUniverse(Universe universe) {
        this.universe = universe;
        return this;
    }

    public void say(String stuff) {
        console.log("Saying: " + stuff);
    }

    public void planet() {
        console.log("Loading new planet...");
        universe.nextPlanet();
        console.log("Planet " + universe.planetSEED + " loaded.");
    }

    public void planet(long SEED) {
        console.log("Loading Planet " + SEED + "...");
        universe.setPlanet(SEED);
        console.log("Planet " + SEED + " loaded.");
    }

    public void character() {
        console.log("Loading new character...");
        universe.nextCharacter();
        console.log("Character " + universe.playerSEED + " loaded.");
    }

    public void character(long SEED) {
        console.log("Loading Character " + SEED + "...");
        universe.setCharacter(SEED);
        console.log("Character " + SEED + " loaded.");
    }
}
