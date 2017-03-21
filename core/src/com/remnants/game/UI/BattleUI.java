package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Entity;
import com.remnants.game.EntityConfig;
import com.remnants.game.Utility;
import com.remnants.game.battle.BattleObserver;
import com.remnants.game.battle.BattleState;
import com.remnants.game.battle.CharacterDrawables;
import com.remnants.game.sfx.ParticleEffectFactory;
import com.remnants.game.sfx.ShakeCamera;

import java.util.Vector;

/**
 * CLASS BattleUI
 *
 * Graphical user interface for the battle scene
 *
 * @extends Window - will be drawn on the screen
 * @implements BattleObserver - notifies other functions of battle updates
 *             CharacterDrawables - interface holding character sprites
 *
 */
public class BattleUI extends Window implements BattleObserver, CharacterDrawables {
    private static final String TAG = BattleUI.class.getSimpleName();

    private Stage _stage;

    //character sprites
    private Vector<AnimatedImage> _enemyImages;
    private Image _tarenBattleSprite = new Image(_tarenBattleDrawable);
    private Image _abellaBattleSprite = new Image(_abellaBattleDrawable);
    private Image _ipoBattleSprite = new Image(_ipoBattleDrawable);
    private Image _tyrusBattleSprite = new Image(_tyrusBattleDrawable);

    //world sprites
    private List<Drawable> _worldSprites;
    private Image _activeWorldSprite;

    private int _currentTurn = 0;

    private final int _enemyWidth = 96;
    private final int _enemyHeight = 96;

    //buttons and labels
    private BattleState _battleState = null;
    private TextButton _attackButton = null;
    private TextButton _runButton = null;
    private TextButton _backButton = null;
    private Label _damageValLabel = null;
    private float _buttonHeight;
    private float _buttonWidth;

    private BitmapFont _messageField;
    private String _message;

    private float _battleTimer = 0;
    private final float _checkTimer = 1;

    //fun to have; original Bludbourne feature
    private ShakeCamera _battleShakeCam = null;
    private Array<ParticleEffect> _effects;
    private float _origDamageValLabelY = 0;
    private Vector2 _currentImagePosition;

    /**
     * CONSTRUCTOR BattleUI
     *
     * @param gameStage - needed for stage height and width for sizing
     */
    public BattleUI(Stage gameStage){
        super("BATTLE", Utility.STATUSUI_SKIN, "solidbackground");

        _stage = new Stage();
        _stage.setViewport(gameStage.getViewport());

        _worldSprites = new List<Drawable>(Utility.STATUSUI_SKIN);
        _worldSprites.setItems(_tarenWorldDrawable, _abellaWorldDrawable, _ipoWorldDrawable, _tyrusWorldDrawable);
        _activeWorldSprite = new Image();

        _messageField = new BitmapFont(Gdx.files.internal("fonts/SDS_6x6.fnt"), false);
        _message = "Do you even Leviosa?";

        //TODO: come up with a good equation to measure the size of the sprites to fit any screen
        float battleSpriteSize = gameStage.getHeight() / 3;
        float enemySpriteSize = gameStage.getHeight() / 4;
        _buttonHeight = gameStage.getHeight() / 4;
        _buttonWidth = (gameStage.getWidth() - _buttonHeight) / 4;

        _battleTimer = 0;
        _battleState = new BattleState();
        _battleState.addObserver(this);

        _effects = new Array<ParticleEffect>();
        _currentImagePosition = new Vector2(0,0);

        _damageValLabel = new Label("0", Utility.STATUSUI_SKIN);
        _damageValLabel.setVisible(false);

        _enemyImages = new Vector<AnimatedImage>();
        //populate _enemyImages with empty AnimatedImage objects
        for (int i = 0; i < 5; i++)
            _enemyImages.add(new AnimatedImage());

        _attackButton = new TextButton("Attack", Utility.STATUSUI_SKIN, "inventory");
        _runButton = new TextButton("Run", Utility.STATUSUI_SKIN, "inventory");
        _backButton = new TextButton("Back", Utility.STATUSUI_SKIN, "inventory");

        float padding = battleSpriteSize * .45f;

        //Enemy table
        Table enemyTable = new Table();
        //enemyTable.setDebug(true);
        enemyTable.align(Align.topLeft).setPosition(0, gameStage.getHeight() - ((gameStage.getHeight() - (enemySpriteSize * 2) - _buttonHeight) / 2));

        for (int i = 0; i < 6; i++) {
            if (i < _enemyImages.size()) {
                enemyTable.add(_enemyImages.get(i)).width(enemySpriteSize).height(enemySpriteSize);
            }
            if (i == 2)
                enemyTable.row();
            if (i > _enemyImages.size())
                enemyTable.add().width(enemySpriteSize).height(enemySpriteSize);
        }

        //Battle Sprite table
        //   splitting it up into two rows allows for easy displacement
        Table topRow = new Table();
        //topRow.setDebug(true);
        topRow.add(_tarenBattleSprite).width(battleSpriteSize).height(battleSpriteSize);
        topRow.add(_ipoBattleSprite).width(battleSpriteSize).height(battleSpriteSize).padRight(padding);
        Table bottomRow = new Table();
        //bottomRow.setDebug(true);
        bottomRow.add(_abellaBattleSprite).width(battleSpriteSize).height(battleSpriteSize).padLeft(padding);
        bottomRow.add(_tyrusBattleSprite).width(battleSpriteSize).height(battleSpriteSize);
        Table bsTable = new Table();
        //bsTable.setDebug(true);
        bsTable.align(Align.topRight);
        bsTable.setPosition(gameStage.getWidth(), gameStage.getHeight());
        bsTable.add(topRow);
        bsTable.row();
        bsTable.add(bottomRow);

        //buttons
        TextButton attackButton = new TextButton("Attack", Utility.STATUSUI_SKIN, "inventory");
        TextButton magicButton = new TextButton("Magic", Utility.STATUSUI_SKIN, "inventory");
        TextButton itemsButton = new TextButton("Items", Utility.STATUSUI_SKIN, "inventory");
        TextButton fleeButton = new TextButton("Flee", Utility.STATUSUI_SKIN, "inventory");
        attackButton.getLabel().setFontScale(4);
        magicButton.getLabel().setFontScale(4);
        itemsButton.getLabel().setFontScale(4);
        fleeButton.getLabel().setFontScale(4);

        //set active world sprite based on who's turn it is
        _activeWorldSprite.setDrawable(_worldSprites.getItems().get(_currentTurn));

        //button table
        Table buttonTable = new Table();
        //buttonTable.setDebug(true);
        buttonTable.add(_activeWorldSprite).height(_buttonHeight).width(_buttonHeight).left();
        buttonTable.add(attackButton).height(_buttonHeight).width(_buttonWidth);
        buttonTable.add(magicButton).height(_buttonHeight).width(_buttonWidth);
        buttonTable.add(itemsButton).height(_buttonHeight).width(_buttonWidth);
        buttonTable.add(fleeButton).height(_buttonHeight).width(_buttonWidth);
        buttonTable.align(Align.topLeft);
        buttonTable.setPosition(0, _buttonHeight);

        _stage.addActor(bsTable);
        _stage.addActor(buttonTable);
        _stage.addActor(enemyTable);

        //layout
        this.setStage(_stage);
        this.setFillParent(true);
        //this.setDebug(true);
        this.pack();

        _origDamageValLabelY = _damageValLabel.getY()+_enemyHeight;

        //button listeners
        attackButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        _message = "Preapring a physical attack";
                        _battleState.characterAttacks();
                        //onNotify(null, BattleEvent.CHARACTER_TURN_DONE);
                    }
                }
        );
        magicButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        _message = "Preparing a magical attack";
                        _battleState.characterAttacks();
                        //onNotify(null, BattleEvent.CHARACTER_TURN_DONE);
                    }
                }
        );
        itemsButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //open inventory
                    }
                }
        );
        fleeButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        _message = "Chickening out already?";
                        debugBattleReady = false;
                        _battleState.playerRuns();
                    }
                }
        );
    }

    public void draw(float delta) {
        Gdx.input.setInputProcessor(_stage);
        _stage.act(delta);
        _stage.draw();

        //draw the message
        _stage.getBatch().begin();
        _messageField.draw(_stage.getBatch(), _message, 0, _buttonHeight + _messageField.getLineHeight());
        _messageField.getData().setScale(3);
        _stage.getBatch().end();
    }

    public void battleZoneTriggered(int battleZoneValue){
        _battleState.setCurrentZoneLevel(battleZoneValue);
    }

    public boolean debugBattleReady = false;

    /**
     * FUNCTION isBattleReady
     *
     * Determines when enemies should appear
     *
     * @return whether we are ready for combat
     */
    public boolean isBattleReady(){
        //TODO: decide when enemies should appear
        /*if( _battleTimer > _checkTimer ){
            _battleTimer = 0;
            return _battleState.isOpponentReady();
        }else{
            return false;
        }*/

        //for debugging
        return debugBattleReady;
    }

    public BattleState getCurrentState(){
        return _battleState;
    }

    /**
     * FUNCTION onNotify - Battle Observer
     *
     * Observes battle changes
     *
     * @param enemies - vector of entities for the enemies spawned in battle
     * @param event - Battle Event being notified
     */
    @Override
    public void onNotify(Vector<Entity> enemies, BattleEvent event) {
        switch(event){
            case PLAYER_TURN_START:
                _runButton.setDisabled(true);
                _runButton.setTouchable(Touchable.disabled);
                _attackButton.setDisabled(true);
                _attackButton.setTouchable(Touchable.disabled);
                _currentTurn = 0;
                _activeWorldSprite.setDrawable(_worldSprites.getItems().get(_currentTurn));
                break;

            case ADD_OPPONENTS:
                for (int i = 0; i < enemies.size(); i++) {
                    _enemyImages.get(i).setEntity(enemies.get(i));
                    _enemyImages.get(i).setCurrentAnimation(Entity.AnimationType.IMMOBILE);
                }
                _message = "Oh look! An enemy!";
                break;

            case OPPONENT_HIT_DAMAGE:
                int damage = Integer.parseInt(enemies.get(0).getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                Gdx.app.log(TAG, "OPPONENT_HIT_DAMAGE: damage: " + damage);
                _message = "Opponent was hit with " + damage + " damage";
                _damageValLabel.setText(String.valueOf(damage));
                _damageValLabel.setY(_origDamageValLabelY);
                //_battleShakeCam.startShaking();
                _damageValLabel.setVisible(true);
                break;

            case OPPONENT_DEFEATED:
                Gdx.app.log(TAG, "Victorious");
                //the screen transitions too soon for this to be seen, but it's fun to have ;)
                _message = "*Female British accent* You are victorious!";
                enemies.clear();
                _damageValLabel.setVisible(false);
                _damageValLabel.setY(_origDamageValLabelY);
                this.setVisible(false);
                break;

            case OPPONENT_TURN_DONE:
                 _attackButton.setDisabled(false);
                 _attackButton.setTouchable(Touchable.enabled);
                _runButton.setDisabled(false);
                _runButton.setTouchable(Touchable.enabled);
                break;

            case OPPONENT_CRIT_ON_FLEE:
                _message = "Enemy attacks while you try to flee!";
                break;

            case CHARACTER_TURN_DONE:
                _currentTurn++;
                if (_currentTurn >= 4) {
                    _currentTurn = 0;
                    _activeWorldSprite.setDrawable(null);
                    onNotify(null, BattleEvent.PLAYER_TURN_DONE);
                }
                else {
                    _activeWorldSprite.setDrawable(_worldSprites.getItems().get(_currentTurn));
                }
                break;

            case PLAYER_TURN_DONE:
                Gdx.app.log(TAG, "All characters have been given an action");
                _battleState.opponentAttacks();
                _currentTurn = 0;
                break;

            case CHARACTER_USED_MAGIC:
                float x = _currentImagePosition.x + (_enemyWidth/2);
                float y = _currentImagePosition.y + (_enemyHeight/2);
                _effects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.WAND_ATTACK, x,y));
                break;

            case PLAYER_RUNNING:
                //don't think there's time for this message to be displayed, but oh well
                _message = "You leave the scene.";
                enemies.clear();
                _currentTurn = 0;
                _activeWorldSprite.setDrawable(_worldSprites.getItems().get(_currentTurn));
                break;

            default:
                break;
        }
    }

    private void setTitle(String s) {
    }

    public void resetDefaults(){
        _battleTimer = 0;
        _battleState.resetDefaults();
        _activeWorldSprite.setDrawable(_worldSprites.getItems().get(0));
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);

        //Draw the particles last
        for( int i = 0; i < _effects.size; i++){
            ParticleEffect effect = _effects.get(i);
            if( effect == null ) continue;
            effect.draw(batch);
        }
    }

    @Override
    public void act(float delta){
        _battleTimer = (_battleTimer + delta)%60;
        if( _damageValLabel.isVisible() && _damageValLabel.getY() < this.getHeight()){
            _damageValLabel.setY(_damageValLabel.getY()+5);
        }

        if( _battleShakeCam != null && _battleShakeCam.isCameraShaking() ){
            Vector2 shakeCoords = _battleShakeCam.getNewShakePosition();
        }

        for( int i = 0; i < _effects.size; i++){
            ParticleEffect effect = _effects.get(i);
            if( effect == null ) continue;
            if( effect.isComplete() ){
                _effects.removeIndex(i);
                effect.dispose();
            }else{
                effect.update(delta);
            }
        }
        super.act(delta);
    }
}