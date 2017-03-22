package com.remnants.game;

import java.util.Hashtable;

public class MapFactory {
    //All maps for the game
    private static Hashtable<MapType,Map> _mapTable = new Hashtable<MapType, Map>();

    public static enum MapType{
        TOP_WORLD,
        MAIN_TOWN,
        TOWN,
        CASTLE_OF_DOOM,
        WORLD_MAP,
        DUNGEON
    }

    static public Map getMap(MapType mapType){
        Map map = null;
        switch(mapType){
            case WORLD_MAP:
                map = _mapTable.get(MapType.WORLD_MAP);
                if(map == null) {
                    map = new WorldMap();
                    _mapTable.put(MapType.WORLD_MAP, map);
                }
                break;
            case TOP_WORLD:
                map = _mapTable.get(MapType.TOP_WORLD);
                if( map == null ){
                    map = new TopWorldMap();
                    _mapTable.put(MapType.TOP_WORLD, map);
                }
                break;
            case MAIN_TOWN:
                map = _mapTable.get(MapType.MAIN_TOWN);
                if (map == null) {
                    map = new MainTownMap();
                    _mapTable.put(MapType.MAIN_TOWN, map);
                }
                break;
            case TOWN:
                map = _mapTable.get(MapType.TOWN);
                if( map == null ){
                    map = new TownMap();
                    _mapTable.put(MapType.TOWN, map);
                }
                break;
            case CASTLE_OF_DOOM:
                map = _mapTable.get(MapType.CASTLE_OF_DOOM);
                if( map == null ){
                    map = new CastleDoomMap();
                    _mapTable.put(MapType.CASTLE_OF_DOOM, map);
                }
                break;
            case DUNGEON:
                map = _mapTable.get(MapType.DUNGEON);
                if( map == null) {
                    map = new DungeonMap();
                    _mapTable.put(MapType.DUNGEON, map);
                }
                break;
            default:
                break;
        }
        return map;
    }

    public static void clearCache(){
        for( Map map: _mapTable.values()){
            map.dispose();
        }
        _mapTable.clear();
    }
}
