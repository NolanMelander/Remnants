package com.remnants.game.battle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by main on 3/7/17.
 */

public interface BattleSprites {
    TextureRegionDrawable _tarenDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/TarenB.png")));
    TextureRegionDrawable _abellaDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/AbellaB.png")));
    TextureRegionDrawable _ipoDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/IpoB.png")));
    TextureRegionDrawable _tyrusDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/TyrusB.png")));
}
