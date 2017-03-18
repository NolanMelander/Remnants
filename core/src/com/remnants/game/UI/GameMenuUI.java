package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Component;
import com.remnants.game.InventoryItem;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.audio.AudioSubject;
import com.remnants.game.battle.CharacterDrawables;
import com.remnants.game.menu.MenuObserver;
import com.remnants.game.menu.MenuState;
import com.remnants.game.profile.ProfileManager;

/**
 * Created by brian on 2/23/2017.
 */

public class GameMenuUI extends Window implements StatusObserver, InventoryObserver, AudioSubject, CharacterDrawables {
    private static final String TAG = GameMenuUI.class.getSimpleName();

    private Stage _stage;
    private MenuState _menuState = null;
    private StatusUI _statusUI;
    private InventoryUI _inventoryUI;

    private Array<AudioObserver> _observers;

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
        _statusUI = new StatusUI();
        _inventoryUI = new InventoryUI();
        _statusUI = new StatusUI();

        //observers
        _observers = new Array<AudioObserver>();
        _statusUI.addObserver(this);
        _inventoryUI.addObserver(this);

        //tables
        Table spriteTable = new Table();
        Table buttonTable = new Table();
        Table spellsTable = new Table();
        Table equipTable = new Table();

        //add tooltips to the stage
        Array<Actor> actors = _inventoryUI.getInventoryActors();
        for(Actor actor : actors){
            _stage.addActor(actor);
        }

        //button creation
        TextButton backButton = new TextButton("Back", Utility.STATUSUI_SKIN);
        TextButton spellButton = new TextButton("Spells", Utility.STATUSUI_SKIN);
        TextButton armorButton = new TextButton("Armor", Utility.STATUSUI_SKIN);
        TextButton weaponButton = new TextButton("Weapons", Utility.STATUSUI_SKIN);
        TextButton accessoryButton = new TextButton("Accessories", Utility.STATUSUI_SKIN);
        TextButton itemButton = new TextButton("Items", Utility.STATUSUI_SKIN);
        TextButton saveButton = new TextButton("Save", Utility.STATUSUI_SKIN);
        TextButton optionButton = new TextButton("Options", Utility.STATUSUI_SKIN);

        //resize button labels
        //  there should be a better way to do this across the board
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

        //set button dimensions
        float buttonHeight = gameStage.getHeight() / 7;
        float buttonWidth = gameStage.getWidth() / 5;

        //set active battle sprite variables
        _absSize = gameStage.getHeight() / 2;
        _absX = buttonWidth / 2;
        _absY = buttonHeight * 2;

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
        _statusUI.setVisible(true);
        _statusUI.setPosition(gameStage.getWidth() * .4f, buttonHeight * 2);
        _statusUI.setMovable(false);
        _statusUI.setHeight(buttonHeight * 4);
        _statusUI.setWidth(gameStage.getWidth() / 4);

        //inventory ui
        _inventoryUI.setVisible(false);
        _inventoryUI.setFillParent(true);

        _stage.addActor(_activeBattleSprite);
        _stage.addActor(backButton);
        _stage.addActor(spellsTable);
        _stage.addActor(buttonTable);
        _stage.addActor(equipTable);
        _stage.addActor(spriteTable);
        _stage.addActor(_statusUI);
        _stage.addActor(_inventoryUI);

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
                                        if (_activeBattleSprite.getDrawable() != null) {
                                            _inventoryUI.setCharacterSprite(_activeBattleSprite);
                                            _inventoryUI.setVisible(true);
                                            _statusUI.setVisible(false);
                                            _inventoryUI.getCurrentState().openInventory();
                                        }
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
                Gdx.app.log(TAG, "Taren's sprite button clicked");
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
                Gdx.app.log(TAG, "Abella's sprite button clicked");
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
                Gdx.app.log(TAG, "Ipo's sprite button clicked");
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
                Gdx.app.log(TAG, "Tyrus's sprite button clicked");
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
                _activeBattleSprite.setDrawable(null);
                _menuState.closeMenu();
            }

        });
    }

    public InventoryUI getInventoryUI() { return _inventoryUI; }

    public void draw(float delta) {
        Gdx.input.setInputProcessor(_stage);

        if (_inventoryUI.getCurrentState().isOpen()) {
            _inventoryUI.setVisible(true);
            _statusUI.setVisible(false);
        }
        else {
            _inventoryUI.setVisible(false);
            _statusUI.setVisible(true);
        }

        _stage.act(delta);
        _stage.draw();
    }

    public MenuState getCurrentState() { return _menuState; }

    public void open() {
        /*if (_inventoryUI.getArmorSlot().hasChildren()) {
            Gdx.app.log(TAG, "Armor slot has something inside:");
            for (Actor actor : _inventoryUI.getArmorSlot().getChildren()) {
                Gdx.app.log(TAG, actor.getName());
            }
        }
        else
            Gdx.app.log(TAG, "Armor slot is as empty as my soul");*/
        _activeArmor.setDrawable(_inventoryUI.getArmorSlot()._customBackgroundDecal.getDrawable());
        //_activeWeapon.setDrawable(_inventoryUI.getWeaponSlot().getTopInventoryItem().getDrawable());
    }

    @Override
    public void onNotify(String name, int value, StatusObserver.StatusEvent event) {
        switch(event) {
            case UPDATED_STAT:
                if (name == "level") {
                    ProfileManager.getInstance().setProperty("currentPlayerLevel", _statusUI.getLevelValue());
                }
                else if (name == "gold") {
                    //_storeInventoryUI.setPlayerGP(value);
                    ProfileManager.getInstance().setProperty("currentPlayerGP", _statusUI.getGoldValue());
                }
                else if (name == "xp") {
                    ProfileManager.getInstance().setProperty("currentPlayerXP", _statusUI.getXPValue());
                }
                else if (name == "hp") {
                    ProfileManager.getInstance().setProperty("currentPlayerHP", _statusUI.getHPValue());
                }
                else if (name == "mp") {
                    ProfileManager.getInstance().setProperty("currentPlayerMP", _statusUI.getMPValue());
                }
                else if (name == "pAtk") {
                    //ProfileManager.getInstance().setProperty("currentPlayerPAtk", _statusUI.getpAtkValue());
                }
                else if (name == "mAtk") {
                    //ProfileManager.getInstance().setProperty("currentPlayerMAtk", _statusUI.getmAtkValue());
                }
                else if (name == "def") {
                    //ProfileManager.getInstance().setProperty("currentPlayerDef", _statusUI.getDefValue());
                }
                else if (name == "agl") {
                    //ProfileManager.getInstance().setProperty("currentPlayerAgl", _statusUI.getAglValue());
                }
                else {
                    Gdx.app.log(TAG, "ERROR: '" + name + "' is not in stat list");
                }
                break;
            case LEVELED_UP:
                notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, InventoryObserver.InventoryEvent event) {
        switch(event){
            case ITEM_CONSUMED:
                String[] strings = value.split(Component.MESSAGE_TOKEN);
                if( strings.length != 2) return;

                int type = Integer.parseInt(strings[0]);
                int typeValue = Integer.parseInt(strings[1]);

                if( InventoryItem.doesRestoreHP(type) ){
                    notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_EATING);
                    _statusUI.addHPValue(typeValue);
                }else if( InventoryItem.doesRestoreMP(type) ){
                    notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_DRINKING);
                    _statusUI.addMPValue(typeValue);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addObserver(AudioObserver audioObserver) {
        _observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        _observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        _observers.removeAll(_observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: _observers){
            observer.onNotify(command, event);
        }
    }
}
