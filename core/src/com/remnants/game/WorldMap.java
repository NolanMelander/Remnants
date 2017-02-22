package com.remnants.game;

import com.remnants.game.audio.AudioObserver;

/**
 * WorldMap
 * Created by Nolan Melander on 2/18/2017.
 * Last Edited by Nolan Melander on 2/21/2017
 * Version 0.2.0
 */

public class WorldMap extends Map {
    private static String _mapPath = "maps/world_map.tmx";

    WorldMap(){
        super(MapFactory.MapType.WORLD_MAP, _mapPath);
    }

    @Override
    public void unloadMusic(){
        notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_OVERWORLD);

    }

    @Override
    public void loadMusic() {
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_OVERWORLD);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_OVERWORLD);
    }

}
