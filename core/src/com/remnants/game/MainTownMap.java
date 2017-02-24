package com.remnants.game;

import com.remnants.game.audio.AudioObserver;

/**
 * MainTownMap
 * Created by Nolan Melander on 2/22/2017
 * Last edited by Nolan Melander on 2/22/2017
 * Version 0.1.0
 */
//TODO Add small door tiles to assets/res/objects
public class MainTownMap extends Map {
    private static String _mapPath = "maps/main_town.tmx";

    MainTownMap() { super(MapFactory.MapType.MAIN_TOWN, _mapPath); }

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
}
