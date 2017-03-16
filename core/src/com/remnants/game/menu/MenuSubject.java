package com.remnants.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by main on 3/10/17.
 */

public class MenuSubject {
    private Array<MenuObserver> _observers;

    public MenuSubject() { _observers = new Array<MenuObserver>(); }

    public void addObserver(MenuObserver menuObserver) { _observers.add(menuObserver); }

    public void removeObserver(MenuObserver menuObserver) { _observers.removeValue(menuObserver, true); }

    protected void notify(final String value, MenuObserver.MenuEvent event) {
        Gdx.app.log("MenuSubject", "Notifying observers");
        for (MenuObserver observer: _observers) {
            observer.onNotify(value, event);
        }
    }
}
