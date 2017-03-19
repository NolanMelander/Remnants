package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.remnants.game.Component;
import com.remnants.game.ComponentObserver;
import com.remnants.game.Entity;
import com.remnants.game.EntityConfig;
import com.remnants.game.InventoryItem;
import com.remnants.game.InventoryItem.ItemTypeID;
import com.remnants.game.MapManager;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioManager;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.audio.AudioSubject;
import com.remnants.game.battle.BattleObserver;
import com.remnants.game.dialog.ConversationGraph;
import com.remnants.game.dialog.ConversationGraphObserver;
import com.remnants.game.menu.MenuObserver;
import com.remnants.game.profile.ProfileManager;
import com.remnants.game.profile.ProfileObserver;
import com.remnants.game.quest.QuestGraph;
import com.remnants.game.screens.MainGameScreen;
import com.remnants.game.sfx.ClockActor;
import com.remnants.game.sfx.ScreenTransitionAction;
import com.remnants.game.sfx.ScreenTransitionActor;
import com.remnants.game.sfx.ShakeCamera;

import java.util.Vector;

public class PlayerHUD implements Screen, AudioSubject, ProfileObserver, ComponentObserver, ConversationGraphObserver, StoreInventoryObserver, BattleObserver, dPadObserver, MenuObserver {
    private static final String TAG = PlayerHUD.class.getSimpleName();

    private Stage _stage;
    private Viewport _viewport;
    private Camera _camera;
    private Entity _player;

    private StatusUI _statusUI;
    private ConversationUI _conversationUI;
    private StoreInventoryUI _storeInventoryUI;
    private QuestUI _questUI;
    private BattleUI _battleUI;
    private dPadUI _padUI;
    private GameMenuUI _menuUI;
    private TextButton _menuButton;

    //for debugging
    private TextButton _debugBattleUIButton;

    private Dialog _messageBoxUI;
    private Json _json;
    private MapManager _mapMgr;

    private Array<AudioObserver> _observers;
    private ScreenTransitionActor _transitionActor;

    private ShakeCamera _shakeCam;
    private ClockActor _clock;

    private static final String INVENTORY_FULL = "Your inventory is full!";

    public PlayerHUD(Camera camera, Entity player, MapManager mapMgr, Remnants game) {
        _camera = camera;
        _player = player;
        _mapMgr = mapMgr;
        _viewport = new ScreenViewport(_camera);
        _stage = new Stage(_viewport);
        //_stage.setDebugAll(true);

        _observers = new Array<AudioObserver>();
        _transitionActor = new ScreenTransitionActor();

        _shakeCam = new ShakeCamera(0,0, 30.0f);

        _json = new Json();
        _messageBoxUI = new Dialog("Message", Utility.STATUSUI_SKIN, "solidbackground"){
            {
                button("OK");
                text(INVENTORY_FULL);
            }
            @Override
            protected void result(final Object object){
                cancel();
                setVisible(false);
            }

        };
        _clock = new ClockActor("0", Utility.STATUSUI_SKIN);
        _clock.setPosition(_stage.getWidth()-_clock.getWidth(),0);
        _clock.setRateOfTime(60);
        _clock.setVisible(true);

        _messageBoxUI.setVisible(false);
        _messageBoxUI.pack();
        _messageBoxUI.setPosition(_stage.getWidth() / 2 - _messageBoxUI.getWidth() / 2
                , _stage.getHeight() / 2 - _messageBoxUI.getHeight() / 2);

        _statusUI = new StatusUI();
        _statusUI.setVisible(true);
        _statusUI.setPosition(0, 0);
        _statusUI.setKeepWithinStage(false);
        _statusUI.setMovable(false);

        _conversationUI = new ConversationUI();
        _conversationUI.setMovable(true);
        _conversationUI.setVisible(false);
        _conversationUI.setPosition(_stage.getWidth() / 2, 0);
        _conversationUI.setWidth(_stage.getWidth() / 2);
        _conversationUI.setHeight(_stage.getHeight() / 2);

        _storeInventoryUI = new StoreInventoryUI();
        _storeInventoryUI.setMovable(false);
        _storeInventoryUI.setVisible(false);
        _storeInventoryUI.setPosition(0, 0);

        _questUI = new QuestUI();
        _questUI.setMovable(false);
        _questUI.setVisible(false);
        _questUI.setKeepWithinStage(false);
        _questUI.setPosition(0, _stage.getHeight() / 2);
        _questUI.setWidth(_stage.getWidth());
        _questUI.setHeight(_stage.getHeight() / 2);

        _battleUI = new BattleUI(_stage);
        _battleUI.setMovable(false);
        //removes all listeners including ones that handle focus
        _battleUI.clearListeners();
        _battleUI.setVisible(false);

        _padUI = new dPadUI();
        _padUI.getGroup().setPosition(_stage.getWidth() / 9, _stage.getHeight() / 5);
        _padUI.getStyle().background.setMinHeight(_stage.getHeight() / 4);
        _padUI.getStyle().background.setMinWidth(_stage.getHeight() / 4);
        _padUI.getStyle().knob.setMinHeight(_stage.getHeight() / 5);
        _padUI.getStyle().knob.setMinWidth(_stage.getHeight() / 5);
        _padUI.setVisible(true);

        _menuUI = new GameMenuUI(_stage);
        _menuUI.setMovable(false);
        _menuUI.setVisible(false);

        _menuButton = new TextButton("Menu", Utility.STATUSUI_SKIN);
        _menuButton.getLabel().setFontScale(3);
        _menuButton.setHeight(_stage.getHeight() / 6);
        _menuButton.setWidth(_stage.getWidth() / 6);
        _menuButton.setPosition((float)(_stage.getWidth() * .8), _stage.getHeight() / 9);

        _debugBattleUIButton = new TextButton("BattleUI", Utility.STATUSUI_SKIN);
        _debugBattleUIButton.getLabel().setFontScale(3);
        _debugBattleUIButton.setHeight(_stage.getHeight() / 6);
        _debugBattleUIButton.setWidth(_stage.getWidth() / 6);
        _debugBattleUIButton.setPosition((float)(_stage.getWidth() * .5), _stage.getHeight() / 9);

        _stage.addActor(_battleUI);
        //_stage.addActor(_questUI);
        //_stage.addActor(_storeInventoryUI);
        //_stage.addActor(_conversationUI);
        //_stage.addActor(_messageBoxUI);
        //_stage.addActor(_statusUI);
        _stage.addActor(_padUI.getGroup());
        _stage.addActor(_menuUI);
        _stage.addActor(_menuButton);
        _stage.addActor(_clock);
        //for debugging
        _stage.addActor(_debugBattleUIButton);

        _battleUI.validate();
        _questUI.validate();
        _storeInventoryUI.validate();
        _conversationUI.validate();
        _messageBoxUI.validate();
        _statusUI.validate();
        _padUI.getGroup().validate();
        _menuUI.validate();
        _menuButton.validate();
        _clock.validate();

        Array<Actor> storeActors = _storeInventoryUI.getInventoryActors();
        for(Actor actor : storeActors ){
            _stage.addActor(actor);
        }

        _stage.addActor(_transitionActor);
        _transitionActor.setVisible(false);

        //Observers
        _player.registerObserver(this);
        _storeInventoryUI.addObserver(this);
        _battleUI.getCurrentState().addObserver(this);
        _menuUI.getCurrentState().addObserver(this);
        this.addObserver(AudioManager.getInstance());

        _menuButton.addListener(new ClickListener() {
           public void clicked(InputEvent event, float x, float y) {
               _menuUI.open();
               onNotify("", MenuEvent.OPEN_MENU);
           }
        });

        //this line shouldn't be necessary
        _player.sendMessage(Component.MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IDLE));

        _padUI.getTouchpad().addListener(new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //Gdx.app.log(TAG, "touchpad touchDown");
                //Gdx.app.log(TAG, "knob percentages: " + _padUI.getTouchpad().getKnobPercentX());
                //        + ", " + _padUI.getTouchpad().getKnobPercentX());
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                //Gdx.app.log(TAG, "touchpad touchDragged");
                //Gdx.app.log(TAG, "knob percentages: " + _padUI.getTouchpad().getKnobPercentX()
                //        + ", " + _padUI.getTouchpad().getKnobPercentX());

                /*
                if (_padUI.isUp())
                    Gdx.app.log(TAG, "touchpad is up");
                if (_padUI.isDown())
                    Gdx.app.log(TAG, "touchpad is down");
                if (_padUI.isLeft())
                    Gdx.app.log(TAG, "touchpad is left");
                if (_padUI.isRight())
                    Gdx.app.log(TAG, "touchpad is right");
                */

                //dPad input
                if(_padUI.isLeft()){
                    _player.sendMessage(Component.MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
                    _player.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, _json.toJson(Entity.Direction.LEFT));
                }else if(_padUI.isRight()){
                    _player.sendMessage(Component.MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
                    _player.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, _json.toJson(Entity.Direction.RIGHT));
                }else if(_padUI.isUp()){
                    _player.sendMessage(Component.MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
                    _player.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, _json.toJson(Entity.Direction.UP));
                }else if(_padUI.isDown()){
                    _player.sendMessage(Component.MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
                    _player.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, _json.toJson(Entity.Direction.DOWN));
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //Gdx.app.log(TAG, "touchpad touchUp");
                //Gdx.app.log(TAG, "knob percentages: " + _padUI.getTouchpad().getKnobPercentX()
                //        + ", " + _padUI.getTouchpad().getKnobPercentX());

                _player.sendMessage(Component.MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IDLE));
                _player.sendMessage(Component.MESSAGE.CURRENT_DIRECTION, _json.toJson(Entity.Direction.DOWN));
            }
        });

        _conversationUI.getCloseButton().addListener(new ClickListener() {
                                                         @Override
                                                         public void clicked(InputEvent event, float x, float y) {
                                                             _conversationUI.setVisible(false);
                                                             _mapMgr.clearCurrentSelectedMapEntity();
                                                         }
                                                     }
        );

        _storeInventoryUI.getCloseButton().addListener(new ClickListener() {
                                                           @Override
                                                           public void clicked(InputEvent event, float x, float y) {
                                                               _storeInventoryUI.savePlayerInventory();
                                                               _storeInventoryUI.cleanupStoreInventory();
                                                               _storeInventoryUI.setVisible(false);
                                                               _mapMgr.clearCurrentSelectedMapEntity();
                                                           }
                                                       }
        );

        _debugBattleUIButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                _battleUI.debugBattleReady = true;
                onNotify("", ComponentEvent.PLAYER_HAS_MOVED);
            }
        });

        //Music/Sound loading
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_PLAYER_PAIN);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_PLAYER_WAND_ATTACK);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_EATING);
        notify(AudioObserver.AudioCommand.SOUND_LOAD, AudioObserver.AudioTypeEvent.SOUND_DRINKING);
        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.BATTLE_UI);
    }

    public Stage getStage() {
        return _stage;
    }

    public ClockActor.TimeOfDay getCurrentTimeOfDay(){
        return _clock.getCurrentTimeOfDay();
    }

    public void updateEntityObservers(){
        _mapMgr.unregisterCurrentMapEntityObservers();
        _questUI.initQuests(_mapMgr);
        _mapMgr.registerCurrentMapEntityObservers(this);
    }

    public void addTransitionToScreen(){
        _transitionActor.setVisible(true);
        _stage.addAction(
                Actions.sequence(
                        Actions.addAction(ScreenTransitionAction.transition(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 1), _transitionActor)));
        showUI();
    }

    private void hideUI() {
        _menuButton.setVisible(false);
        _padUI.setVisible(false);
        _debugBattleUIButton.setVisible(false);
    }

    private void showUI() {
        Gdx.app.log(TAG, "ShowUI entered");
        _menuButton.setVisible(true);
        _padUI.setVisible(true);
        _debugBattleUIButton.setVisible(true);
    }

    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {
        switch(event){
            case PROFILE_LOADED:
                Gdx.app.log(TAG, "Loading profile...");
                boolean firstTime = profileManager.getIsNewProfile();

                if( firstTime ){
                    InventoryUI.clearInventoryItems(_menuUI.getInventoryUI().getInventorySlotTable());
                    InventoryUI.clearInventoryItems(_menuUI.getInventoryUI().getEquipSlotTable());
                    _menuUI.getInventoryUI().resetEquipSlots();

                    _questUI.setQuests(new Array<QuestGraph>());

                    //add default items if first time
                    Array<ItemTypeID> items = _player.getEntityConfig().getInventory();
                    Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
                    for( int i = 0; i < items.size; i++){
                        itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
                    }
                    InventoryUI.populateInventory(_menuUI.getInventoryUI().getInventorySlotTable(), itemLocations, _menuUI.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                    profileManager.setProperty("playerInventory", InventoryUI.getInventory(_menuUI.getInventoryUI().getInventorySlotTable()));

                    //start the player with some money
                    _statusUI.setGoldValue(20);
                    _statusUI.setStatusForLevel(1);

                    _clock.setTotalTime(60 * 60 * 12); //start at noon
                    profileManager.setProperty("currentTime", _clock.getTotalTime());
                }else{
                    int goldVal = profileManager.getProperty("currentPlayerGP", Integer.class);

                    Array<InventoryItemLocation> inventory = profileManager.getProperty("playerInventory", Array.class);
                    if (inventory.size == 0) {
                        Gdx.app.log(TAG, "No inventory items loaded from profile");
                    }
                    else {
                        Gdx.app.log(TAG, "Loaded items from profile:");
                        for (InventoryItemLocation iil : inventory) {
                            Gdx.app.log(TAG, iil.getItemNameProperty());
                        }
                    }
                    InventoryUI.populateInventory(_menuUI.getInventoryUI().getInventorySlotTable(), inventory, _menuUI.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);

                    Array<InventoryItemLocation> equipInventory = profileManager.getProperty("playerEquipInventory", Array.class);
                    if( equipInventory != null && equipInventory.size > 0 ){
                        _menuUI.getInventoryUI().resetEquipSlots();
                        InventoryUI.populateInventory(_menuUI.getInventoryUI().getEquipSlotTable(), equipInventory, _menuUI.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                    }

                    Array<QuestGraph> quests = profileManager.getProperty("playerQuests", Array.class);
                    _questUI.setQuests(quests);

                    //TODO: update to include all stats
                    int xpMaxVal = profileManager.getProperty("currentPlayerXPMax", Integer.class);
                    int xpVal = profileManager.getProperty("currentPlayerXP", Integer.class);

                    int hpMaxVal = profileManager.getProperty("currentPlayerHPMax", Integer.class);
                    int hpVal = profileManager.getProperty("currentPlayerHP", Integer.class);

                    int mpMaxVal = profileManager.getProperty("currentPlayerMPMax", Integer.class);
                    int mpVal = profileManager.getProperty("currentPlayerMP", Integer.class);

                    //check if this will crash
                    int pAtkVal = profileManager.getProperty("currentPlayerPAtkMax", Integer.class);

                    int levelVal = profileManager.getProperty("currentPlayerLevel", Integer.class);

                    //set the current max values first
                    _statusUI.setXPValueMax(xpMaxVal);
                    _statusUI.setHPValueMax(hpMaxVal);
                    _statusUI.setMPValueMax(mpMaxVal);

                    _statusUI.setXPValue(xpVal);
                    _statusUI.setHPValue(hpVal);
                    _statusUI.setMPValue(mpVal);

                    //then add in current values
                    _statusUI.setGoldValue(goldVal);
                    _statusUI.setLevelValue(levelVal);

                    float totalTime = profileManager.getProperty("currentTime", Float.class);
                    _clock.setTotalTime(totalTime);
                }

            break;
            case SAVING_PROFILE:
                Gdx.app.log(TAG, "Saving profile...");
                /*if (_menuUI.getInventoryUI().getInventorySlotTable().getChildren().size == 0)
                    Gdx.app.log(TAG, "No children in the inventory slot table to save");
                else {
                    Gdx.app.log(TAG, "Items in inventory slot table being saved:");
                    for (int i = 0; i < _menuUI.getInventoryUI().getInventorySlotTable().getCells().size; i++) {
                        Gdx.app.log(TAG, "Count: " + i);
                        Gdx.app.log(TAG, _menuUI.getInventoryUI().getInventorySlotTable().getCells().get(i).toString());
                    }
                }*/
                profileManager.setProperty("playerQuests", _questUI.getQuests());
                profileManager.setProperty("playerInventory", InventoryUI.getInventory(_menuUI.getInventoryUI().getInventorySlotTable()));
                profileManager.setProperty("playerEquipInventory", InventoryUI.getInventory(_menuUI.getInventoryUI().getEquipSlotTable()));
                profileManager.setProperty("currentPlayerGP", _statusUI.getGoldValue() );
                profileManager.setProperty("currentPlayerLevel", _statusUI.getLevelValue() );
                profileManager.setProperty("currentPlayerXP", _statusUI.getXPValue() );
                profileManager.setProperty("currentPlayerXPMax", _statusUI.getXPValueMax() );
                profileManager.setProperty("currentPlayerHP", _statusUI.getHPValue() );
                profileManager.setProperty("currentPlayerHPMax", _statusUI.getHPValueMax() );
                profileManager.setProperty("currentPlayerMP", _statusUI.getMPValue() );
                profileManager.setProperty("currentPlayerMPMax", _statusUI.getMPValueMax() );
                profileManager.setProperty("currentPlayerPAtkMax", _statusUI.getpAtkValueMax());
                profileManager.setProperty("currentPlayerPAtk", _statusUI.getpAtkValue());
                profileManager.setProperty("currentPlayerMAtkMax", _statusUI.getmAtkValueMax());
                profileManager.setProperty("currentPlayerMAtk", _statusUI.getmAtkValue());
                profileManager.setProperty("currentPlayerDefMax", _statusUI.getDefValueMax());
                profileManager.setProperty("currentPlayerDef", _statusUI.getDefValue());
                profileManager.setProperty("currentPlayerAglMax", _statusUI.getAglValueMax());
                profileManager.setProperty("currentPlayerAgl", _statusUI.getAglValue());
                profileManager.setProperty("currentTime", _clock.getTotalTime());
                break;
            case CLEAR_CURRENT_PROFILE:
                Gdx.app.log(TAG, "Clearing current profile...");
                profileManager.setProperty("playerQuests", new Array<QuestGraph>());
                profileManager.setProperty("playerInventory", new Array<InventoryItemLocation>());
                profileManager.setProperty("playerEquipInventory", new Array<InventoryItemLocation>());
                profileManager.setProperty("currentPlayerGP", 0 );
                profileManager.setProperty("currentPlayerLevel",0 );
                profileManager.setProperty("currentPlayerXP", 0 );
                profileManager.setProperty("currentPlayerXPMax", 0 );
                profileManager.setProperty("currentPlayerHP", 0 );
                profileManager.setProperty("currentPlayerHPMax", 0 );
                profileManager.setProperty("currentPlayerMP", 0 );
                profileManager.setProperty("currentPlayerMPMax", 0 );
                profileManager.setProperty("currentPlayerPAtkMax", 0);
                profileManager.setProperty("currentPlayerPAtk", 0);
                profileManager.setProperty("currentPlayerMAtkMax", 0);
                profileManager.setProperty("currentPlayerMAtk", 0);
                profileManager.setProperty("currentPlayerDefMax", 0);
                profileManager.setProperty("currentPlayerDef", 0);
                profileManager.setProperty("currentPlayerAglMax", 0);
                profileManager.setProperty("currentPlayerAgl", 0);
                profileManager.setProperty("currentTime", 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, ComponentEvent event) {
        switch(event) {
            case LOAD_CONVERSATION:
                EntityConfig config = _json.fromJson(EntityConfig.class, value);

                //Check to see if there is a version loading into properties
                if( config.getItemTypeID().equalsIgnoreCase(InventoryItem.ItemTypeID.NONE.toString()) ) {
                    EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(config.getEntityID(), EntityConfig.class);
                    if( configReturnProperty != null ){
                        config = configReturnProperty;
                    }
                }

                _conversationUI.loadConversation(config);
                _conversationUI.getCurrentConversationGraph().addObserver(this);
                break;
            case SHOW_CONVERSATION:
                EntityConfig configShow = _json.fromJson(EntityConfig.class, value);

                if( configShow.getEntityID().equalsIgnoreCase(_conversationUI.getCurrentEntityID())) {
                    _conversationUI.setVisible(true);
                }
                break;
            case HIDE_CONVERSATION:
                EntityConfig configHide = _json.fromJson(EntityConfig.class, value);
                if( configHide.getEntityID().equalsIgnoreCase(_conversationUI.getCurrentEntityID())) {
                    _conversationUI.setVisible(false);
                }
                break;
            case QUEST_LOCATION_DISCOVERED:
                String[] string = value.split(Component.MESSAGE_TOKEN);
                String questID = string[0];
                String questTaskID = string[1];


                _questUI.questTaskComplete(questID, questTaskID);
                updateEntityObservers();
                break;
            case ENEMY_SPAWN_LOCATION_CHANGED:
                String enemyZoneID = value;
                _battleUI.battleZoneTriggered(Integer.parseInt(enemyZoneID));
                break;
            case PLAYER_HAS_MOVED:
                if( _battleUI.isBattleReady() ){
                    Gdx.app.log(TAG, "Entering battle mode");
                    Gdx.app.log(TAG, "Setting opponents");
                    _battleUI.getCurrentState().setCurrentOpponents();
                    addTransitionToScreen();
                    MainGameScreen.setGameState(MainGameScreen.GameState.SAVING);
                    _mapMgr.disableCurrentmapMusic();
                    notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                    hideUI();
                    _battleUI.toBack();
                    _battleUI.setVisible(true);
                    _battleUI.debugBattleReady = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(ConversationGraph graph, ConversationCommandEvent event) {
        switch(event) {
            case LOAD_STORE_INVENTORY:
                Entity selectedEntity = _mapMgr.getCurrentSelectedMapEntity();
                if( selectedEntity == null ){
                    break;
                }

                Array<InventoryItemLocation> inventory =  InventoryUI.getInventory(_menuUI.getInventoryUI().getInventorySlotTable());
                _storeInventoryUI.loadPlayerInventory(inventory);

                Array<InventoryItem.ItemTypeID> items  = selectedEntity.getEntityConfig().getInventory();
                Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
                for( int i = 0; i < items.size; i++){
                    itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.STORE_INVENTORY));
                }

                _storeInventoryUI.loadStoreInventory(itemLocations);

                _conversationUI.setVisible(false);
                _storeInventoryUI.toFront();
                _storeInventoryUI.setVisible(true);
                break;
            case EXIT_CONVERSATION:
                _conversationUI.setVisible(false);
                _mapMgr.clearCurrentSelectedMapEntity();
                break;
            case ACCEPT_QUEST:
                Entity currentlySelectedEntity = _mapMgr.getCurrentSelectedMapEntity();
                if( currentlySelectedEntity == null ){
                    break;
                }
                EntityConfig config = currentlySelectedEntity.getEntityConfig();

                QuestGraph questGraph = _questUI.loadQuest(config.getQuestConfigPath());

                if( questGraph != null ){
                    //Update conversation dialog
                    config.setConversationConfigPath(QuestUI.RETURN_QUEST);
                    config.setCurrentQuestID(questGraph.getQuestID());
                    ProfileManager.getInstance().setProperty(config.getEntityID(), config);
                    updateEntityObservers();
                }

                _conversationUI.setVisible(false);
                _mapMgr.clearCurrentSelectedMapEntity();
                break;
            case RETURN_QUEST:
                Entity returnEntity = _mapMgr.getCurrentSelectedMapEntity();
                if( returnEntity == null ){
                    break;
                }
                EntityConfig configReturn = returnEntity.getEntityConfig();

                EntityConfig configReturnProperty = ProfileManager.getInstance().getProperty(configReturn.getEntityID(), EntityConfig.class);
                if( configReturnProperty == null ) return;

                String questID = configReturnProperty.getCurrentQuestID();

                if( _questUI.isQuestReadyForReturn(questID) ){
                    notify(AudioObserver.AudioCommand.MUSIC_PLAY_ONCE, AudioObserver.AudioTypeEvent.MUSIC_LEVEL_UP_FANFARE);
                    QuestGraph quest = _questUI.getQuestByID(questID);
                    _statusUI.addXPValue(quest.getXpReward());
                    _statusUI.addGoldValue(quest.getGoldReward());
                    notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
                    _menuUI.getInventoryUI().removeQuestItemFromInventory(questID);
                    configReturnProperty.setConversationConfigPath(QuestUI.FINISHED_QUEST);
                    ProfileManager.getInstance().setProperty(configReturnProperty.getEntityID(), configReturnProperty);
                }

                _conversationUI.setVisible(false);
                _mapMgr.clearCurrentSelectedMapEntity();

                break;
            case ADD_ENTITY_TO_INVENTORY:
                Entity entity = _mapMgr.getCurrentSelectedMapEntity();
                if( entity == null ){
                    break;
                }

                if( _menuUI.getInventoryUI().doesInventoryHaveSpace() ){
                    _menuUI.getInventoryUI().addEntityToInventory(entity, entity.getEntityConfig().getCurrentQuestID());
                    _mapMgr.clearCurrentSelectedMapEntity();
                    _conversationUI.setVisible(false);
                    entity.unregisterObservers();
                    _mapMgr.removeMapQuestEntity(entity);
                    _questUI.updateQuests(_mapMgr);
                }else{
                    _mapMgr.clearCurrentSelectedMapEntity();
                    _conversationUI.setVisible(false);
                    _messageBoxUI.setVisible(true);
                }

                break;
            case NONE:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, StoreInventoryEvent event) {
        switch (event) {
            case PLAYER_GP_TOTAL_UPDATED:
                int val = Integer.valueOf(value);
                _statusUI.setGoldValue(val);
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_COIN_RUSTLE);
                break;
            case PLAYER_INVENTORY_UPDATED:
                Array<InventoryItemLocation> items = _json.fromJson(Array.class, value);
                InventoryUI.populateInventory(_menuUI.getInventoryUI().getInventorySlotTable(), items, _menuUI.getInventoryUI().getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(int value, dPadEvent event) {
        switch(event) {
            case LEFT:
                break;
            case RIGHT:
                break;
            case UP:
                break;
            case DOWN:
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        _shakeCam.reset();
    }

    @Override
    public void render(float delta) {
        if( _shakeCam.isCameraShaking() ){
            Vector2 shakeCoords = _shakeCam.getNewShakePosition();
            _camera.position.x = shakeCoords.x + _stage.getWidth()/2;
            _camera.position.y = shakeCoords.y + _stage.getHeight()/2;
        }
        _stage.act(delta);
        _stage.draw();

        if (_menuUI.isVisible())
            _menuUI.draw(delta);

        if (_battleUI.isVisible())
            _battleUI.draw(delta);
    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        _battleUI.resetDefaults();
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        _stage.dispose();
    }

    @Override
    public void onNotify(Vector<Entity> enemyEntity, BattleEvent event) {
        switch (event) {
            case OPPONENT_HIT_DAMAGE:
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_CREATURE_PAIN);
                break;
            case OPPONENT_DEFEATED:
                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
                //TODO: update for multiple enemies
                /*
                int goldReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_GP_REWARD.toString()));
                _statusUI.addGoldValue(goldReward);
                int xpReward = Integer.parseInt(enemyEntity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_XP_REWARD.toString()));
                _statusUI.addXPValue(xpReward);
                */
                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                _mapMgr.enableCurrentmapMusic();
                addTransitionToScreen();
                _battleUI.setVisible(false);
                showUI();
                Gdx.input.setInputProcessor(_stage);
                break;
            case PLAYER_RUNNING:
                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
                notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                _mapMgr.enableCurrentmapMusic();
                addTransitionToScreen();
                Gdx.input.setInputProcessor(_stage);
                //change visibilities to show the buttons and touchpad
                _battleUI.setVisible(false);
                showUI();
                break;
            case PLAYER_HIT_DAMAGE:
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_PLAYER_PAIN);
                int hpVal = ProfileManager.getInstance().getProperty("currentPlayerHP", Integer.class);
                _statusUI.setHPValue(hpVal);
                _shakeCam.startShaking();

                if( hpVal <= 0 ){
                    _shakeCam.reset();
                    notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_BATTLE);
                    addTransitionToScreen();
                    _battleUI.setVisible(false);
                    MainGameScreen.setGameState(MainGameScreen.GameState.GAME_OVER);
                }
                break;
            case CHARACTER_USED_MAGIC:
                notify(AudioObserver.AudioCommand.SOUND_PLAY_ONCE, AudioObserver.AudioTypeEvent.SOUND_PLAYER_WAND_ATTACK);
                int mpVal = ProfileManager.getInstance().getProperty("currentPlayerMP", Integer.class);
                _statusUI.setMPValue(mpVal);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNotify(String value, MenuObserver.MenuEvent event) {
        switch(event) {
            case OPEN_MENU:
                Gdx.app.log(TAG, "Opening Game Menu");
                MainGameScreen.setGameState(MainGameScreen.GameState.PAUSED);
                _mapMgr.enableCurrentmapMusic();
                _menuUI.setVisible(true);
                hideUI();
                break;
            case CLOSE_MENU:
                Gdx.app.log(TAG, "Closing Game Menu");
                MainGameScreen.setGameState(MainGameScreen.GameState.RUNNING);
                _mapMgr.enableCurrentmapMusic();
                _menuUI.setVisible(false);
                showUI();
                Gdx.input.setInputProcessor(_stage);
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
