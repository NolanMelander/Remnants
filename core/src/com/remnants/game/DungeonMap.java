package com.remnants.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.profile.ProfileManager;

/**
 * Created by Nolan Melander on 3/22/2017.
 * Last Edited by Nolan Melander on 3/22/2017
 * Version: 0.0.1
 */

public class DungeonMap extends Map {
    private static String _mapPath = "maps/dungeon.tmx";
    private Json _json;

    DungeonMap() {
        super(MapFactory.MapType.DUNGEON, _mapPath);

        _json = new Json();
    }

    @Override
    public void unloadMusic(){
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_DUNGEON);
    }

    @Override
    public void loadMusic(){
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_DUNGEON);//MUSIC_DUNGEON);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_DUNGEON);
    }
}
