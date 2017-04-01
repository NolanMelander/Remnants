package com.remnants.game.menu;

/**
 * Created by brian evans on 3/10/17.
 */

public interface MenuObserver {
    public static enum MenuEvent {
        OPEN_MENU,
        CLOSE_MENU
    }

    void onNotify(final String value, MenuEvent event);
}
