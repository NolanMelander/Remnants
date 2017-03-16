package com.remnants.game.UI;


public interface StatusObserver {
    public static enum StatusEvent {
        UPDATED_STAT,
        LEVELED_UP
    }

    void onNotify(final String name, final int value, StatusEvent event);
}
