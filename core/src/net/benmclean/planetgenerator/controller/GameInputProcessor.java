package net.benmclean.planetgenerator.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import net.benmclean.planetgenerator.model.Direction;
import net.benmclean.planetgenerator.model.Universe;
import net.benmclean.planetgenerator.view.GameScreen;

import java.util.*;

public class GameInputProcessor implements InputProcessor {
    public Universe universe;
    public GameScreen screen;

    public GameInputProcessor(Universe universe, GameScreen screen) {
        this.universe = universe;
        this.screen = screen;
        for (int i = 0; i < TRACKED_KEYS_ARRAY.size(); i++)
            keyPressed[i] = false;
    }

    public static final double REPEAT_RATE = 0.05;
    private double timeSinceRepeat = 0;

    public final static ArrayList<Integer> TRACKED_KEYS_ARRAY = new ArrayList<Integer>(Arrays.asList(
            Input.Keys.UP,
            Input.Keys.DOWN,
            Input.Keys.LEFT,
            Input.Keys.RIGHT,
            Input.Keys.SPACE,
            Input.Keys.ESCAPE,
            Input.Keys.ENTER,
            Input.Keys.ALT_LEFT,
            Input.Keys.ALT_RIGHT,
            Input.Keys.X,
            Input.Keys.Z,
            Input.Keys.S,
            Input.Keys.A
    ));
    public final static Set<Integer> TRACKED_KEYS = Collections.unmodifiableSet(
            new HashSet<Integer>(TRACKED_KEYS_ARRAY));
    public boolean[] keyPressed = new boolean[TRACKED_KEYS_ARRAY.size()];

    public static int keyInt(int keycode) {
        for (int x = 0; x < TRACKED_KEYS_ARRAY.size(); x++)
            if (TRACKED_KEYS_ARRAY.get(x) == keycode)
                return x;
        throw new ArrayIndexOutOfBoundsException();  // This keycode is not contained in TRACKED_KEYS_ARRAY
    }

    @Override
    public boolean keyDown(int keycode) {
        if (TRACKED_KEYS.contains(keycode)) keyPressed[keyInt(keycode)] = true;

        if (keycode == Input.Keys.ENTER && (keyPressed[keyInt(Input.Keys.ALT_LEFT)] || keyPressed[keyInt(Input.Keys.ALT_RIGHT)]))
            screen.toggleFullscreen();
        if (keycode == Input.Keys.S) universe.inShip = !universe.inShip;
        if (keycode == Input.Keys.A) universe.showMap = !universe.showMap;
        if (keycode == Input.Keys.X) universe.nextPlanet();
        if (keycode == Input.Keys.Z) universe.nextCharacter();
        timeSinceRepeat = 0;
        moveFromInput(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (TRACKED_KEYS.contains(keycode)) keyPressed[keyInt(keycode)] = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void tick(float delta) {
        timeSinceRepeat += delta;
        if (timeSinceRepeat >= REPEAT_RATE) {
            timeSinceRepeat = 0;
            for (int key : TRACKED_KEYS)
                if (keyPressed[keyInt(key)])
                    moveFromInput(key);
        }
        if (keyPressed[keyInt(Input.Keys.DOWN)]) {
            if (keyPressed[keyInt(Input.Keys.RIGHT)])
                universe.direction = Direction.SOUTHEAST;
            else if (keyPressed[keyInt(Input.Keys.LEFT)])
                universe.direction = Direction.SOUTHWEST;
            else
                universe.direction = Direction.SOUTH;
        } else if (keyPressed[keyInt(Input.Keys.UP)]) {
            if (keyPressed[keyInt(Input.Keys.RIGHT)])
                universe.direction = Direction.NORTHEAST;
            else if (keyPressed[keyInt(Input.Keys.LEFT)])
                universe.direction = Direction.NORTHWEST;
            else
                universe.direction = Direction.NORTH;
        } else if (keyPressed[keyInt(Input.Keys.RIGHT)])
            universe.direction = Direction.EAST;
        else if (keyPressed[keyInt(Input.Keys.LEFT)])
            universe.direction = Direction.WEST;
    }

    public void moveFromInput(int keycode) {
        //boolean moved = true;
        switch (keycode) {
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Input.Keys.UP:
                universe.movePlayer(Direction.NORTH);
                break;
            case Input.Keys.RIGHT:
                universe.movePlayer(Direction.EAST);
                break;
            case Input.Keys.DOWN:
                universe.movePlayer(Direction.SOUTH);
                break;
            case Input.Keys.LEFT:
                universe.movePlayer(Direction.WEST);
                break;
            default:
                //moved=false;
                break;
        }
//        if (moved) universe.endTurn();
    }
}
