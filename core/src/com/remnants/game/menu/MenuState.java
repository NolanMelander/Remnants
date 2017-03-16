package com.remnants.game.menu;

import com.badlogic.gdx.Gdx;

/**
 * Created by main on 3/10/17.
 */

public class MenuState extends MenuSubject {
    private static final String TAG = MenuState.class.getSimpleName();

    //TODO: get the profile information to display in the UI

    public void closeMenu() {
        Gdx.app.log(TAG, "Closing Game Menu");
        notify("", MenuObserver.MenuEvent.CLOSE_MENU);
    }
}
