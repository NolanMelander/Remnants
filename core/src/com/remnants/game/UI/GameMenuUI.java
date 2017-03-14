package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;
import com.remnants.game.battle.CharacterDrawables;
import com.remnants.game.menu.MenuObserver;
import com.remnants.game.menu.MenuState;

/**
 * Created by brian on 2/23/2017.
 */

public class GameMenuUI extends Window implements MenuObserver, CharacterDrawables {
    private static final String TAG = GameMenuUI.class.getSimpleName();

    private Stage _stage;
    private MenuState _menuState = null;
    private StatusUI _statusUI;
    private InventoryUI _inventoryUI;

    //active images
    private Image _activeBattleSprite = new Image();
    private Image _activeArmor = new Image();
    private Image _activeWeapon = new Image();
    private Image _activeAccessory = new Image();

    //active battle sprite variables
    private float _absSize;
    private float _absX;
    private float _absY;

    public GameMenuUI (Stage gameStage) {
        super ("MENU", Utility.STATUSUI_SKIN, "solidbackground");

        //initial creation
        _stage = new Stage();
        _menuState = new MenuState();
        _menuState.addObserver(this);
        Table spriteTable = new Table();
        Table buttonTable = new Table();
        Table spellsTable = new Table();
        Table equipTable = new Table();

        //set button dimensions
        float buttonHeight = gameStage.getHeight() / 7;
        float buttonWidth = gameStage.getWidth() / 5;

        //set active battle sprite variables
        _absSize = gameStage.getHeight() / 2;
        _absX = buttonWidth / 2;
        _absY = buttonHeight * 2;

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
        _activeArmor = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/yellow-tunic-plain.png"))));
        _activeWeapon = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("skins/temp/broad-sword.png"))));

        //back button
        backButton.setWidth(buttonWidth);
        backButton.setHeight(buttonHeight);
        backButton.setPosition(0, gameStage.getHeight() - buttonHeight);

        //battle sprite
        _activeBattleSprite.setHeight(_absSize);
        _activeBattleSprite.setWidth(_absSize);
        _activeBattleSprite.setPosition(_absX, _absY);

        //set sprites to fill button square
        tarenSpriteButton.getImageCell().expand().fill();
        abellaSpriteButton.getImageCell().expand().fill();
        ipoSpriteButton.getImageCell().expand().fill();
        tyrusSpriteButton.getImageCell().expand().fill();

        //button table layout
        //buttonTable.setDebug(true);
        buttonTable.top().right();
        buttonTable.setPosition(gameStage.getWidth(), gameStage.getHeight());
        buttonTable.add(spellButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(armorButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(weaponButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(accessoryButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(itemButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(saveButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(optionButton).width(buttonWidth).height(buttonHeight).row();

        //spells table layout
        //spellsTable.setDebug(true);
        spellsTable.top().right();
        spellsTable.setPosition(gameStage.getWidth() - buttonWidth, gameStage.getHeight());
        spellsTable.add().width(buttonHeight).height(buttonHeight);
        spellsTable.add(spellBook1).width(buttonHeight).height(buttonHeight);
        spellsTable.add(spellBook2).width(buttonHeight).height(buttonHeight);

        //equipment table layout
        //equipTable.setDebug(true);
        equipTable.top().right();
        equipTable.setPosition(gameStage.getWidth() - buttonWidth, gameStage.getHeight() - buttonHeight);
        equipTable.add(_activeArmor).width(buttonHeight).height(buttonHeight).row();
        equipTable.add(_activeWeapon).width(buttonHeight).height(buttonHeight).row();
        equipTable.add(/*accessory*/).width(buttonHeight).height(buttonHeight).row();

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
        _statusUI.setPosition(gameStage.getWidth() * .4f, buttonHeight * 2);
        _statusUI.setMovable(false);
        _statusUI.setHeight(buttonHeight * 4);
        _statusUI.setWidth(gameStage.getWidth() / 4);

        //inventory ui
        _inventoryUI = new InventoryUI();
        _inventoryUI.setVisible(false);
        _inventoryUI.setFillParent(true);

        _stage.addActor(_activeBattleSprite);
        _stage.addActor(backButton);
        _stage.addActor(spellsTable);
        _stage.addActor(buttonTable);
        _stage.addActor(equipTable);
        _stage.addActor(spriteTable);
        _stage.addActor(_statusUI);
        //_stage.addActor(_inventoryUI);

        this.setStage(_stage);
        this.setFillParent(true);
        this.setDebug(true);
        this.pack();

        //Button Listeners
        spellButton.addListener(new ClickListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        return true;
                                    }

                                    @Override
                                    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                                        //_game.setScreen(_game.getScreenType(Remnants.ScreenType.SpellScreen));
                                        _inventoryUI.setVisible(true);
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
                _activeBattleSprite.setHeight(_absSize);
                _activeBattleSprite.setWidth(_absSize);
                _activeBattleSprite.setPosition(_absX, _absY);
                _activeBattleSprite.setDrawable(_tarenBattleDrawable);
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
                _activeBattleSprite.setHeight(_absSize);
                _activeBattleSprite.setWidth(_absSize);
                _activeBattleSprite.setPosition(_absX, _absY);
                _activeBattleSprite.setDrawable(_abellaBattleDrawable);

                //set stats to display Abella's stats
            }

        });

        ipoSpriteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _activeBattleSprite.setHeight(_absSize / 2);
                _activeBattleSprite.setWidth(_absSize / 2);
                _activeBattleSprite.setPosition(_absX + _absSize / 4, _absY);
                _activeBattleSprite.setDrawable(_ipoBattleDrawable);
                //set stats to display Ipo's stats
            }

        });

        tyrusSpriteButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _activeBattleSprite.setHeight(_absSize);
                _activeBattleSprite.setWidth(_absSize);
                _activeBattleSprite.setPosition(_absX, _absY);
                _activeBattleSprite.setDrawable(_tyrusBattleDrawable);
                //set stats to display Tyrus's stats
            }

        });

        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _activeBattleSprite = new Image();
                _menuState.closeMenu();
            }

        });
    }

    public void draw(float delta) {
        Gdx.input.setInputProcessor(_stage);
        _stage.act(delta);
        _stage.draw();
    }

    public MenuState getCurrentState() { return _menuState; }

    @Override
    public void onNotify(String value, MenuEvent event) {
        //no need to do anything here
    }
}
