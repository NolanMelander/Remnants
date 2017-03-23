package com.remnants.game.battle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by main on 3/7/17.
 */

public interface CharacterDrawables {
    //battle sprites
    TextureRegionDrawable _tarenBattleDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/TarenB.png")));
    TextureRegionDrawable _abellaBattleDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/AbellaB.png")));
    TextureRegionDrawable _ipoBattleDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/IpoB.png")));
    TextureRegionDrawable _tyrusBattleDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/TyrusB.png")));

    //world sprites
    //TODO: set image paths for world drawables
    TextureRegionDrawable _tarenWorldDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/TarenB.png")));
    TextureRegionDrawable _abellaWorldDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/AbellaB.png")));
    TextureRegionDrawable _ipoWorldDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/IpoB.png")));
    TextureRegionDrawable _tyrusWorldDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/TyrusB.png")));
}
