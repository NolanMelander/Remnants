package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Component;
import com.remnants.game.InventoryItem;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.audio.AudioSubject;
import com.remnants.game.battle.CharacterDrawables;
import com.remnants.game.menu.MenuState;
import com.remnants.game.profile.ProfileManager;

/**
 * Created by brian on 2/23/2017.
 */

/**
 * CLASS GameMenuUI
 *
 * Graphical user interface for the in-game menu. Opened with a button on the PlayerHUD
 *
 * @extends Window - will be drawn on the screen
 * @implements StatusObserver - displays the Status UI
 *             InventoryObserver - displays character's equipped items
 *             AudioSubject - notifies for audio changes
 *             CharacterDrawables - draws battle and world sprites
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
    private Image _activeSpellScroll = new Image();
    private Image _activeAccessory = new Image();

    //active battle sprite variables
    private float _absSize;
    private float _absX;
    private float _absY;

    private float _buttonHeight;
    private float _buttonWidth;

    private BitmapFont _messageField;
    private String _message;

    /**
     * CONSTRUCTOR GameMenuUI
     *
     * @param gameStage - required for sizing
     */
    public GameMenuUI (Stage gameStage) {
        super ("MENU", Utility.STATUSUI_SKIN, "solidbackground");

        //initial creation
        _stage = new Stage();
        _menuState = new MenuState();
        _statusUI = new StatusUI();
        _inventoryUI = new InventoryUI();
        _statusUI = new StatusUI();
        _messageField = new BitmapFont(Gdx.files.internal("fonts/SDS_6x6.fnt"), false);
        _message = "Menu";

        //observers
        _observers = new Array<AudioObserver>();
        _statusUI.addObserver(this);
        _inventoryUI.addObserver(this);

        //tables
        Table spriteTable = new Table();
        Table buttonTable = new Table();
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

        //set button dimensions
        _buttonHeight = gameStage.getHeight() / 7;
        _buttonWidth = gameStage.getWidth() / 5;

        //set active battle sprite variables
        _absSize = gameStage.getHeight() / 2;
        _absX = _buttonWidth / 2;
        _absY = _buttonHeight * 2;

        //back button
        backButton.setWidth(_buttonWidth);
        backButton.setHeight(_buttonHeight);
        backButton.setPosition(0, gameStage.getHeight() - _buttonHeight);

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
        buttonTable.add(spellButton).width(_buttonWidth).height(_buttonHeight).row();
        buttonTable.add(armorButton).width(_buttonWidth).height(_buttonHeight).row();
        buttonTable.add(weaponButton).width(_buttonWidth).height(_buttonHeight).row();
        buttonTable.add(accessoryButton).width(_buttonWidth).height(_buttonHeight).row();
        buttonTable.add(itemButton).width(_buttonWidth).height(_buttonHeight).row();
        buttonTable.add(saveButton).width(_buttonWidth).height(_buttonHeight).row();
        buttonTable.add(optionButton).width(_buttonWidth).height(_buttonHeight).row();

        //equipment table layout
        //equipTable.setDebug(true);
        equipTable.top().right();
        equipTable.setPosition(gameStage.getWidth() - _buttonWidth, gameStage.getHeight());
        equipTable.add(_activeSpellScroll).width(_buttonHeight).height(_buttonHeight).row();
        equipTable.add(_activeArmor).width(_buttonHeight).height(_buttonHeight).row();
        equipTable.add(_activeWeapon).width(_buttonHeight).height(_buttonHeight).row();
        equipTable.add(/*accessory*/).width(_buttonHeight).height(_buttonHeight).row();

        float spritePadding = _buttonHeight - (_buttonHeight * 0.15f);

        //sprite table layout
        //spriteTable.setDebug(true);
        spriteTable.left().bottom();
        spriteTable.padBottom(_buttonHeight/2).padLeft(spritePadding);
        spriteTable.add(tarenSpriteButton).width(_buttonHeight).height(_buttonHeight).padRight(spritePadding);
        spriteTable.add(abellaSpriteButton).width(_buttonHeight).height(_buttonHeight).padRight(spritePadding);
        spriteTable.add(ipoSpriteButton).width(_buttonHeight).height(_buttonHeight).padRight(spritePadding);
        spriteTable.add(tyrusSpriteButton).width(_buttonHeight).height(_buttonHeight).padRight(spritePadding);

        //status ui
        _statusUI.setVisible(true);
        _statusUI.setPosition(gameStage.getWidth() * .4f, _buttonHeight * 2);
        _statusUI.setMovable(false);
        _statusUI.setHeight(_buttonHeight * 4);
        _statusUI.setWidth(gameStage.getWidth() / 4);

        //inventory ui
        _inventoryUI.setVisible(false);
        _inventoryUI.setFillParent(true);

        _stage.addActor(_activeBattleSprite);
        _stage.addActor(backButton);
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
                if (_activeBattleSprite.getDrawable() != null) {
                    _inventoryUI.setCharacterSprite(_activeBattleSprite);
                    _inventoryUI.setVisible(true);
                    _statusUI.setVisible(false);
                    _inventoryUI.getCurrentState().openInventory();
                }
            }


        });

        weaponButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (_activeBattleSprite.getDrawable() != null) {
                    _inventoryUI.setCharacterSprite(_activeBattleSprite);
                    _inventoryUI.setVisible(true);
                    _statusUI.setVisible(false);
                    _inventoryUI.getCurrentState().openInventory();
                }
            }


        });

        accessoryButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (_activeBattleSprite.getDrawable() != null) {
                    _inventoryUI.setCharacterSprite(_activeBattleSprite);
                    _inventoryUI.setVisible(true);
                    _statusUI.setVisible(false);
                    _inventoryUI.getCurrentState().openInventory();
                }
            }


        });

        itemButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (_activeBattleSprite.getDrawable() != null) {
                    _inventoryUI.setCharacterSprite(_activeBattleSprite);
                    _inventoryUI.setVisible(true);
                    _statusUI.setVisible(false);
                    _inventoryUI.getCurrentState().openInventory();
                }
            }


        });

        saveButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _message = "Game saved";
                ProfileManager.getInstance().saveProfile();
            }
        });

        optionButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _message = "Options unavailable";
            }


        });

        tarenSpriteButton.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Taren's sprite button clicked");
                _activeBattleSprite.setDrawable(_tarenBattleDrawable);
                _message = "Taren";
            }

        });

        abellaSpriteButton.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Abella's sprite button clicked");
                _activeBattleSprite.setDrawable(_abellaBattleDrawable);
                _message = "Abella";
            }

        });

        ipoSpriteButton.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Ipo's sprite button clicked");
                _activeBattleSprite.setDrawable(_ipoBattleDrawable);
                _message = "Ipo";
            }

        });

        tyrusSpriteButton.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.log(TAG, "Tyrus's sprite button clicked");
                _activeBattleSprite.setDrawable(_tyrusBattleDrawable);
                _message = "Tyrus";
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
        updateItemImages();

        //draw the message
        _stage.getBatch().begin();
        _messageField.draw(_stage.getBatch(), _message, _buttonWidth,  _stage.getHeight() - _messageField.getLineHeight());
        _messageField.getData().setScale(4.5f);
        _stage.getBatch().end();
    }

    public MenuState getCurrentState() { return _menuState; }

    /**
     * FUNCTION updateImages
     * updates the item images so that inventory changes are automatically displayed
     */
    private void updateItemImages() {
        //armor
        if (_inventoryUI.getArmorSlot().getTopInventoryItem() == null)
            _activeArmor.setDrawable(_inventoryUI.getArmorSlot()._customBackgroundDecal.getDrawable());
        else
            _activeArmor.setDrawable(_inventoryUI.getArmorSlot().getTopInventoryItem().getDrawable());

        //weapon
        if (_inventoryUI.getWeaponSlot().getTopInventoryItem() == null)
            _activeWeapon.setDrawable(_inventoryUI.getWeaponSlot()._customBackgroundDecal.getDrawable());
        else
            _activeWeapon.setDrawable(_inventoryUI.getWeaponSlot().getTopInventoryItem().getDrawable());

        //spell scroll
        if (_inventoryUI.getSpellSlot().getTopInventoryItem() == null)
            _activeSpellScroll.setDrawable(_inventoryUI.getSpellSlot()._customBackgroundDecal.getDrawable());
        else
            _activeSpellScroll.setDrawable(_inventoryUI.getSpellSlot().getTopInventoryItem().getDrawable());
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
                _message = "Leveled up!";
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
