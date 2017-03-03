package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;

/**
 * Created by brian on 2/23/2017.
 */

public class GameMenuUI implements Screen {
    private static final String TAG = GameMenuUI.class.getSimpleName();

    private Remnants _game;
    private Stage _stage;
    private Image _activeBattleSprite = new Image();
    private StatusUI _statusUI;

    public GameMenuUI (Remnants game) {
        //initial creation
        _game = game;
        _stage = new Stage();
        Table table = new Table();
        Table spriteTable = new Table();
        table.setFillParent(true);

        float buttonHeight = _stage.getHeight() / 7;
        float buttonWidth = _stage.getWidth() / 5;

        //button creation
        TextButton backButton = new TextButton("Back", Utility.STATUSUI_SKIN);
        TextButton spellButton = new TextButton("Spells", Utility.STATUSUI_SKIN);
        TextButton armorButton = new TextButton("Armor", Utility.STATUSUI_SKIN);
        TextButton weaponButton = new TextButton("Weapons", Utility.STATUSUI_SKIN);
        TextButton accessoryButton = new TextButton("Accessories", Utility.STATUSUI_SKIN);
        TextButton itemButton = new TextButton("Items", Utility.STATUSUI_SKIN);
        TextButton saveButton = new TextButton("Save", Utility.STATUSUI_SKIN);
        TextButton optionButton = new TextButton("Options", Utility.STATUSUI_SKIN);

        backButton.getLabel().setFontScale(3);
        spellButton.getLabel().setFontScale(3);
        armorButton.getLabel().setFontScale(3);
        weaponButton.getLabel().setFontScale(3);
        accessoryButton.getLabel().setFontScale(3);
        itemButton.getLabel().setFontScale(3);
        saveButton.getLabel().setFontScale(3);
        optionButton.getLabel().setFontScale(3);

        ImageButton tarenSpriteButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/taren.png"))));
        ImageButton abellaSpriteButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/abella.png"))));
        ImageButton ipoSpriteButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/ipo.png"))));
        ImageButton tyrusSpriteButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/tyrus.png"))));

        //temporary images
        Image spellBook1 = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/ice-crystal-scroll.png"))));
        Image spellBook2 = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/flame-scroll.png"))));
        Image armor = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/yellow-tunic-plain.png"))));
        Image weapon = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/broad-sword.png"))));

        final TextureRegionDrawable abellaDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/abella-battle-sprite.png")));

        //back button
        backButton.setWidth(buttonWidth);
        backButton.setHeight(buttonHeight);
        backButton.setPosition(0, _stage.getHeight() - buttonHeight);

        //battle sprite
        _activeBattleSprite.setHeight(_stage.getHeight() / 2);
        _activeBattleSprite.setWidth(_stage.getHeight() / 2);
        _activeBattleSprite.setPosition(buttonWidth / 2, buttonHeight * 2);

        //set sprites to fill button square
        tarenSpriteButton.getImageCell().expand().fill();
        abellaSpriteButton.getImageCell().expand().fill();
        ipoSpriteButton.getImageCell().expand().fill();
        tyrusSpriteButton.getImageCell().expand().fill();

        //table layout
        //table.setDebug(true);
        table.top().right();
        table.add(spellBook1).width(buttonHeight).height(buttonHeight);
        table.add(spellBook2).width(buttonHeight).height(buttonHeight);
        table.add(spellButton).width(buttonWidth).height(buttonHeight).row();
        table.add();
        table.add(armor).width(buttonHeight).height(buttonHeight);
        table.add(armorButton).width(buttonWidth).height(buttonHeight).row();
        table.add();
        table.add(weapon).width(buttonHeight).height(buttonHeight);
        table.add(weaponButton).width(buttonWidth).height(buttonHeight).row();
        table.add();
        table.add();
        table.add(accessoryButton).width(buttonWidth).height(buttonHeight).row();
        table.add();
        table.add();
        table.add(itemButton).width(buttonWidth).height(buttonHeight).row();
        table.add();
        table.add();
        table.add(saveButton).width(buttonWidth).height(buttonHeight).row();
        table.add();
        table.add();
        table.add(optionButton).width(buttonWidth).height(buttonHeight).row();

        float spritePadding = buttonHeight - (buttonHeight * 0.15f);

        //sprite table layout
        //spriteTable.setDebug(true);
        spriteTable.left().bottom();
        spriteTable.padBottom(buttonHeight/2).padLeft(spritePadding);
        spriteTable.add(tarenSpriteButton).width(buttonHeight).height(buttonHeight).padRight(spritePadding);
        spriteTable.add(abellaSpriteButton).width(buttonHeight).height(buttonHeight).padRight(spritePadding);
        spriteTable.add(ipoSpriteButton).width(buttonHeight).height(buttonHeight).padRight(spritePadding);
        spriteTable.add(tyrusSpriteButton).width(buttonHeight).height(buttonHeight).padRight(spritePadding);

        //status ui
        _statusUI = new StatusUI();
        _statusUI.setVisible(true);
        _statusUI.setPosition(_stage.getWidth() * .4f, buttonHeight * 2);
        _statusUI.setMovable(false);
        _statusUI.setHeight(buttonHeight * 4);
        _statusUI.setWidth(_stage.getWidth() / 4);

        _stage.addActor(_activeBattleSprite);
        _stage.addActor(backButton);
        _stage.addActor(table);
        _stage.addActor(spriteTable);
        _stage.addActor(_statusUI);

        //Button Listeners
        spellButton.addListener(new ClickListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                                        return true;
                                    }

                                    @Override
                                    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                                        //_game.setScreen(_game.getScreenType(Remnants.ScreenType.SpellScreen));
                                    }


        });

        armorButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //_game.setScreen(_game.getScreenType(Remnants.ScreenType.ArmorScreen));
            }


        });

        weaponButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                //_game.setScreen(_game.getScreenType(Remnants.ScreenType.WeaponScreen));
            }


        });

        accessoryButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                //_game.setScreen(_game.getScreenType(Remnants.ScreenType.AccessoryScreen));
            }


        });

        itemButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                //_game.setScreen(_game.getScreenType(Remnants.ScreenType.ItemScreen));
            }


        });

        saveButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                //_game.setScreen(_game.getScreenType(Remnants.ScreenType.SaveScreen));
            }


        });

        optionButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                //_game.setScreen(_game.getScreenType(Remnants.ScreenType.OptionScreen));
            }


        });

        tarenSpriteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //_activeBattleSprite.setDrawable(tarenDrawable);
                //set stats to display Taren's stats
            }


        });

        abellaSpriteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _activeBattleSprite.setDrawable(abellaDrawable);
                //set stats to display Taren's stats
            }


        });

        ipoSpriteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //_activeBattleSprite.setDrawable(ipoDrawable);
                //set stats to display Taren's stats
            }


        });

        tyrusSpriteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //_activeBattleSprite.setDrawable(tyrusDrawable);
                //set stats to display Taren's stats
            }


        });

        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _game.setScreen(_game.getScreenType(Remnants.ScreenType.MainGame));
            }


        });
    }

    @Override
    public void dispose() {}

    @Override
    public void pause() {}

    @Override
    public void render(float delta) {
        //this shouldn't have to be called here...
        //   but it won't work without it
        Gdx.input.setInputProcessor(_stage);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void resume() {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

}
