package com.remnants.game.battle;

import com.badlogic.gdx.utils.Array;
import com.remnants.game.Entity;

import java.util.Vector;

public class BattleSubject {
    private Array<BattleObserver> _observers;

    public BattleSubject(){
        _observers = new Array<BattleObserver>();
    }

    public void addObserver(BattleObserver battleObserver){
        _observers.add(battleObserver);
    }

    public void removeObserver(BattleObserver battleObserver){
        _observers.removeValue(battleObserver, true);
    }

    protected void notify(final Vector<Entity> entities, BattleObserver.BattleEvent event){
        for(BattleObserver observer: _observers){
            observer.onNotify(entities, event);
        }
    }
}
