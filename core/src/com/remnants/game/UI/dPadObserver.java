package com.remnants.game.UI;

/**
 * Created by brian on 2/19/2017.
 */

public interface dPadObserver {
    public static enum dPadEvent {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    void onNotify(final int value, dPadEvent event);
}
