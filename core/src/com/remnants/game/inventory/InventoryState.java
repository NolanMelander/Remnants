package com.remnants.game.inventory;

import com.remnants.game.menu.MenuState;

/**
 * Created by main on 3/18/17.
 */

public class InventoryState {
    private static final String TAG = MenuState.class.getSimpleName();

    private boolean open = false;

    public boolean isOpen() { return open; }

    public InventoryState() {
        open = false;
    }

    public void openInventory() {
        open = true;
    }

    public void closeInventory() {
        open = false;
    }
}
