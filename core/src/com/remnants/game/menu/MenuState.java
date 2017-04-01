package com.remnants.game.menu;

import com.badlogic.gdx.Gdx;

/**
 * Created by brian evans on 3/10/17.
 */

public class MenuState extends MenuSubject {
    private static final String TAG = MenuState.class.getSimpleName();

    public void closeMenu() {
        Gdx.app.log(TAG, "Closing Game Menu");
        notify("", MenuObserver.MenuEvent.CLOSE_MENU);
    }
}
