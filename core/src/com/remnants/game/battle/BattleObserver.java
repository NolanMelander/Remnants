package com.remnants.game.battle;

import com.remnants.game.Entity;

import java.util.Vector;

public interface BattleObserver {
    public static enum BattleEvent{
        ADD_OPPONENTS,
        OPPONENT_HIT_DAMAGE,
        OPPONENT_DEFEATED,
        OPPONENT_TURN_DONE,
        OPPONENT_CRIT_ON_FLEE,
        PLAYER_HIT_DAMAGE,
        PLAYER_RUNNING,
        PLAYER_TURN_DONE,
        PLAYER_TURN_START,
        CHARACTER_USED_MAGIC,
        CHARACTER_TURN_DONE,
        ALL_OPPONENTS_DONE,
        NONE
    }

    void onNotify(final Vector<Entity> enemyEntities, BattleEvent event);
}
