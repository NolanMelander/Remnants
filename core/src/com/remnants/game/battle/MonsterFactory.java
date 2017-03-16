package com.remnants.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Entity;
import com.remnants.game.EntityConfig;

import java.util.Hashtable;
import java.util.Vector;

public class MonsterFactory {
    private static final String TAG = MonsterFactory.class.getSimpleName();

    public static enum MonsterEntityType{
        MONSTER001,MONSTER002,MONSTER003,MONSTER004,MONSTER005,
        MONSTER006,MONSTER007,MONSTER008,MONSTER009,MONSTER010,
        MONSTER011,MONSTER012,MONSTER013,MONSTER014,MONSTER015,
        MONSTER016,MONSTER017,MONSTER018,MONSTER019,MONSTER020,
        MONSTER021,MONSTER022,MONSTER023,MONSTER024,MONSTER025,
        MONSTER026,MONSTER027,MONSTER028,MONSTER029,MONSTER030,
        MONSTER031,MONSTER032,MONSTER033,MONSTER034,MONSTER035,
        MONSTER036,MONSTER037,MONSTER038,MONSTER039,MONSTER040,
        MONSTER041, MONSTER042,
        NONE
    }

    private static MonsterFactory _instance = null;
    private Hashtable<String, Entity> _entities;
    private Hashtable<String, Array<MonsterEntityType>> _monsterZones;

    private MonsterFactory(){
        Array<EntityConfig> configs = Entity.getEntityConfigs("scripts/monsters.json");
        _entities =  Entity.initEntities(configs);

        _monsterZones = MonsterZone.getMonsterZones("scripts/monster_zones.json");
    }

    public static MonsterFactory getInstance() {
        if (_instance == null) {
            _instance = new MonsterFactory();
        }

        return _instance;
    }

    public Entity getMonster(MonsterEntityType monsterEntityType){
        Entity entity = _entities.get(monsterEntityType.toString());
        return new Entity(entity);
    }

    public Vector<Entity> getRandomMonsters(int monsterZoneID){
        Vector<Entity> enemies = new Vector<Entity>();
        int numCreatures = MathUtils.random(1,5);
        for(int i = 0; i < numCreatures; i++) {
            //TODO: figure out how to get the right monsterZoneID
            Gdx.app.log(TAG, "MonsterZoneID: " + monsterZoneID);
            Array<MonsterEntityType> monsters = _monsterZones.get(String.valueOf(1/*monsterZoneID*/));

            if (_monsterZones.get(String.valueOf(1)) == null) {
                Gdx.app.log(TAG, "MonsterZones is returning null");
            }

            int size = monsters.size;

            if (size == 0) {
                return null;
            }
            int randomIndex = MathUtils.random(size - 1);

            enemies.add(getMonster(monsters.get(randomIndex)));
            Gdx.app.log(TAG, "Adding " + getMonster(monsters.get(randomIndex)).getEntityConfig().getEntityID());
        }

        Gdx.app.log(TAG, "Number of monsters generated: " + enemies.size());
        return enemies;
    }

}
