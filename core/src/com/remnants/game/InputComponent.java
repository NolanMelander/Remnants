package com.remnants.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;

public abstract class InputComponent extends ComponentSubject implements Component, InputProcessor {

    protected Entity.Direction _currentDirection = null;
    protected Entity.State _currentState = null;
    protected Json _json;

    protected enum Keys {
        LEFT, RIGHT, UP, DOWN, QUIT, PAUSE
    }

    protected enum Mouse {
        SELECT, DOACTION
    }

    protected enum dPad {
        LEFT, RIGHT, UP, DOWN
    }

    protected static Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
    protected static Map<Mouse, Boolean> mouseButtons = new HashMap<Mouse, Boolean>();
    protected static Map<dPad, Boolean> dPadButtons = new HashMap<dPad, Boolean>();

    //initialize the hashmap for inputs
    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);
        keys.put(Keys.PAUSE, false);
    };

    static {
        mouseButtons.put(Mouse.SELECT, false);
        mouseButtons.put(Mouse.DOACTION, false);
    };

    static {
        dPadButtons.put(dPad.LEFT, false);
        dPadButtons.put(dPad.RIGHT, false);
        dPadButtons.put(dPad.UP, false);
        dPadButtons.put(dPad.DOWN, false);
    };

    InputComponent(){
        _json = new Json();
    }

    public abstract void update(Entity entity, float delta);

}