package com.remnants.game.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class LevelTable {
    private String levelID;
    private String charName;
    private int hpMax;     //health points
    private int mpMax;     //magic points
    private int mAtkMax;      //magic attack
    private int pAtkMax;      //physical attack
    private int defMax;       //defense
    private int aglMax;       //agility
    private int xpMax;     //experience points

    public String getLevelID() {
        return levelID;
    }
    public void setLevelID(String levelID) {
        this.levelID = levelID;
    }

    public String getCharName() { return charName; }
    public void setCharName(String name) { this.charName = name; }

    public int getHpMax() {
        return hpMax;
    }
    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
    }

    public int getMpMax() {
        return mpMax;
    }
    public void setMpMax(int mpMax) {
        this.mpMax = mpMax;
    }

    public int getmAtkMax() { return mAtkMax; }
    public void setmAtkMax(int mAtk) { this.mAtkMax = mAtk; }

    public int getpAtkMax() { return pAtkMax; }
    public void setpAtkMax(int pAtk) { this.pAtkMax = pAtk; }

    public int getDefMax() { return defMax; }
    public void setDefMax(int def) { this.defMax = def; }

    public int getAglMax() { return aglMax; }
    public void setAglMax(int agl) { this.aglMax = agl; }

    public int getXpMax() {
        return xpMax;
    }
    public void setXpMax(int xpMax) {
        this.xpMax = xpMax;
    }

    static public Array<LevelTable> getLevelTables(String configFilePath){
        Json json = new Json();
        Array<LevelTable> levelTable = new Array<LevelTable>();

        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));

        for (JsonValue jsonVal : list) {
            LevelTable table = json.readValue(LevelTable.class, jsonVal);
            levelTable.add(table);
        }

        return levelTable;
    }
}
