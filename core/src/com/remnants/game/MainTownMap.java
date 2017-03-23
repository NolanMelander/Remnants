package com.remnants.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.profile.ProfileManager;

/**
 * MainTownMap
 * Created by Nolan Melander on 2/22/2017
 * Last edited by Nolan Melander on 3/4/2017
 * Version 1.1.0
 */
public class MainTownMap extends Map {
    private static String _mapPath = "maps/main_town.tmx";
    private Json _json;
    MainTownMap() {
        super(MapFactory.MapType.MAIN_TOWN, _mapPath);

        _json = new Json();

        Entity vendor = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.TOWN_VENDOR);
        initSpecialEntityPosition(vendor);
        _mapEntities.add(vendor);

        Entity king = EntityFactory.getInstance().getEntityByName(EntityFactory.EntityName.KING);
        initSpecialEntityPosition(king);
        _mapEntities.add(king);
    }

    //TODO Find music for town, currently set to use the same as the world map
    @Override
    public void unloadMusic(){
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_MAINTOWN);

    }

    @Override
    public void loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_MAINTOWN);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_MAINTOWN);
    }


    private void initSpecialEntityPosition(Entity entity){
        Vector2 position = new Vector2(0,0);

        if( _specialNPCStartPositions.containsKey(entity.getEntityConfig().getEntityID()) ) {
            position = _specialNPCStartPositions.get(entity.getEntityConfig().getEntityID());
        }
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, _json.toJson(position));

        //Overwrite default if special config is found
        EntityConfig entityConfig = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), EntityConfig.class);
        if( entityConfig != null ){
            entity.setEntityConfig(entityConfig);
        }
    }
}
