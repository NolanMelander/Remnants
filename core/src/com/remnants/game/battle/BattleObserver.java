package com.remnants.game.battle;

import com.remnants.game.Entity;

public interface BattleObserver {
    public static enum BattleEvent{
        OPPONENT_ADDED,
        OPPONENT_HIT_DAMAGE,
        OPPONENT_DEFEATED,
        OPPONENT_TURN_DONE,
        PLAYER_HIT_DAMAGE,
        PLAYER_RUNNING,
        PLAYER_TURN_DONE,
        PLAYER_TURN_START,
        PLAYER_USED_MAGIC,
        CHARACTER_TURN_DONE,
        ALL_OPPONENTS_DONE,
        NONE
    }

    void onNotify(final Entity enemyEntity, BattleEvent event);
}
