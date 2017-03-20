package com.remnants.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.remnants.game.Entity;
import com.remnants.game.EntityConfig;
import com.remnants.game.UI.InventoryObserver;
import com.remnants.game.profile.ProfileManager;

import java.util.Vector;

public class BattleState extends BattleSubject implements InventoryObserver {
    private static final String TAG = BattleState.class.getSimpleName();

    private Vector<Entity> _enemies;
    private int _currentZoneLevel = 1;
    private int _currentCharacterAP;
    private int _currentCharacterDP;
    private int _currentCharacterWandAPPoints = 0;
    private final int _chanceOfAttack = 25;
    private final int _chanceOfEscape = 40;
    private final int _criticalChance = 90;
    private Timer.Task _characterAttackCalculations;
    private Timer.Task _opponentAttackCalculations;
    private Timer.Task _checkCharacterMagicUse;

    /**
     * CONSTRUCTOR BattleState
     */
    public BattleState(){
        _characterAttackCalculations = getCharacterAttackCalculationTimer();
        _opponentAttackCalculations = getOpponentAttackCalculationTimer();
        _checkCharacterMagicUse = getCharacterMagicUseCheckTimer();
    }

    public void resetDefaults(){
        Gdx.app.debug(TAG, "Resetting defaults...");
        _currentZoneLevel = 0;
        _currentCharacterAP = 10;
        _currentCharacterDP = 10;
        _currentCharacterWandAPPoints = 10;
        _characterAttackCalculations.cancel();
        _opponentAttackCalculations.cancel();
        _checkCharacterMagicUse.cancel();
    }

    public void setCurrentZoneLevel(int zoneLevel){
        _currentZoneLevel = zoneLevel;
    }

    public int getCurrentZoneLevel(){
        return _currentZoneLevel;
    }

    public boolean isOpponentReady(){
        if( _currentZoneLevel == 0 ) return false;
        int randomVal = MathUtils.random(1,100);

        //Gdx.app.debug(TAG, "CHANGE OF ATTACK: " + _chanceOfAttack + " randomval: " + randomVal);

        //if( _chanceOfAttack > randomVal  ){
            setCurrentOpponents();
            return true;
        //}else{
            //return false;
        //}
    }

    /**
     * FUNCTION setCurrentOpponents
     *
     * Adds new monsters for each encounter
     */
    public void setCurrentOpponents(){
        Gdx.app.log(TAG, "Entered BATTLE ZONE: " + _currentZoneLevel);
        //for only one monster
        _enemies = new Vector<Entity>();
        _enemies.add(MonsterFactory.getInstance().getMonster(MonsterFactory.MonsterEntityType.MONSTER001));
        //for multiple random monsters
        //_enemies = MonsterFactory.getInstance().getRandomMonsters(_currentZoneLevel);
        notify(_enemies, BattleObserver.BattleEvent.ADD_OPPONENTS);
    }

    public void characterAttacks(){
        if( _enemies == null ){
            return;
        }

        //Check for magic if used in attack; If we don't have enough MP, then return
        int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
        //notify(_currentOpponent, BattleObserver.BattleEvent.PLAYER_TURN_START);

        if( _currentCharacterWandAPPoints == 0 ){
            if( !_characterAttackCalculations.isScheduled() ){
                Timer.schedule(_characterAttackCalculations, 1);
            }
        }else if(_currentCharacterWandAPPoints > mpVal ){
            BattleState.this.notify(_enemies, BattleObserver.BattleEvent.CHARACTER_TURN_DONE);
            return;
        }else{
            if( !_checkCharacterMagicUse.isScheduled() && !_characterAttackCalculations.isScheduled() ){
                Timer.schedule(_checkCharacterMagicUse, .5f);
                Timer.schedule(_characterAttackCalculations, 1);
            }
        }
    }

    /**
     * FUNCTION opponentAttacks
     * Initiates the opponent's attack calculation timer
     */
    public void opponentAttacks(){
        if( _enemies == null ){
            return;
        }

        if( !_opponentAttackCalculations.isScheduled() ){
            Timer.schedule(_opponentAttackCalculations, 1);
        }
    }

    private Timer.Task getCharacterMagicUseCheckTimer(){
        return new Timer.Task() {
            @Override
            public void run() {
                int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
                mpVal -= _currentCharacterWandAPPoints;
                ProfileManager.getInstance().setProperty("currentPlayerMP", mpVal);
                BattleState.this.notify(_enemies, BattleObserver.BattleEvent.CHARACTER_USED_MAGIC);
            }
        };
    }

    private Timer.Task getCharacterAttackCalculationTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int currentOpponentHP = Integer.parseInt(_enemies.get(0).getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));
                int currentOpponentDP = Integer.parseInt(_enemies.get(0).getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_DEFENSE_POINTS.toString()));

                int damage = MathUtils.clamp(_currentCharacterAP - currentOpponentDP, 0, _currentCharacterAP);
                //for debugging
                damage = 4;

                Gdx.app.log(TAG, "ENEMY HAS " + currentOpponentHP + " hit with damage: " + damage);

                currentOpponentHP = MathUtils.clamp(currentOpponentHP - damage, 0, currentOpponentHP);
                _enemies.get(0).getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString(), String.valueOf(currentOpponentHP));

                Gdx.app.log(TAG, "Player attacks " + _enemies.get(0).getEntityConfig().getEntityID() + " leaving it with HP: " + currentOpponentHP);

                _enemies.get(0).getEntityConfig().setPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString(), String.valueOf(damage));
                if( damage > 0 ){
                    BattleState.this.notify(_enemies, BattleObserver.BattleEvent.OPPONENT_HIT_DAMAGE);
                }

                if (currentOpponentHP == 0) {
                    Gdx.app.log(TAG, "Notifying that the enemy is dead");
                    BattleState.this.notify(_enemies, BattleObserver.BattleEvent.OPPONENT_DEFEATED);
                }

                BattleState.this.notify(_enemies, BattleObserver.BattleEvent.CHARACTER_TURN_DONE);
            }
        };
    }

    private Timer.Task getOpponentAttackCalculationTimer() {
        return new Timer.Task() {
            @Override
            public void run() {
                int currentOpponentHP = Integer.parseInt(_enemies.get(0).getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HEALTH_POINTS.toString()));

                if (currentOpponentHP <= 0) {
                    BattleState.this.notify(_enemies, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);
                    return;
                }

                int currentOpponentAP = Integer.parseInt(_enemies.get(0).getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_ATTACK_POINTS.toString()));
                int damage = MathUtils.clamp(currentOpponentAP - _currentCharacterDP, 0, currentOpponentAP);
                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                hpVal = MathUtils.clamp( hpVal - damage, 0, hpVal);
                ProfileManager.getInstance().setProperty("currentPlayerHP", hpVal);

                if( damage > 0 ) {
                    BattleState.this.notify(_enemies, BattleObserver.BattleEvent.PLAYER_HIT_DAMAGE);
                }

                Gdx.app.debug(TAG, "Player HIT for " + damage + " BY " + _enemies.get(0).getEntityConfig().getEntityID() + " leaving player with HP: " + hpVal);

                BattleState.this.notify(_enemies, BattleObserver.BattleEvent.OPPONENT_TURN_DONE);
            }
        };
    }

    /**
    * FUNCTION playerRuns
    *
    * Compares a random number between 1 and 100 with the chance of escape
    * There is a chance to be attacked when trying to flee
     */
    public void playerRuns(){
        int randomVal = MathUtils.random(1,100);
        if( _chanceOfEscape > randomVal  ) {
            notify(_enemies, BattleObserver.BattleEvent.PLAYER_RUNNING);
        }else if (randomVal > _criticalChance){
            notify(_enemies, BattleObserver.BattleEvent.OPPONENT_CRIT_ON_FLEE);
            opponentAttacks();
        }else{
            return;
        }
    }

    /**
     * FUNCTION onNotify - Inventory Observer
     *
     * Observes inventory changes
     *
     * @param value - AP or DP value as a string
     * @param event - Inventory event being notified
     */
    @Override
    public void onNotify(String value, InventoryEvent event) {
        switch(event) {
            case UPDATED_AP:
                int apVal = Integer.valueOf(value);
                _currentCharacterAP = 50;//apVal;
                //Gdx.app.debug(TAG, "APVAL: " + _currentPlayerAP);
                break;
            case UPDATED_DP:
                int dpVal = Integer.valueOf(value);
                _currentCharacterDP = 50;//dpVal;
                //Gdx.app.debug(TAG, "DPVAL: " + _currentPlayerDP);
                break;
            case ADD_WAND_AP:
                int wandAP = Integer.valueOf(value);
                _currentCharacterWandAPPoints += wandAP;
                Gdx.app.debug(TAG, "WandAP: " + _currentCharacterWandAPPoints);
                break;
            case REMOVE_WAND_AP:
                int removeWandAP = Integer.valueOf(value);
                _currentCharacterWandAPPoints -= removeWandAP;
                Gdx.app.debug(TAG, "WandAP: " + _currentCharacterWandAPPoints);
                break;
            default:
                break;
        }
    }
}
