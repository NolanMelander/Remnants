package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Entity;
import com.remnants.game.EntityConfig;
import com.remnants.game.Utility;
import com.remnants.game.battle.BattleObserver;
import com.remnants.game.battle.BattleState;
import com.remnants.game.battle.CharacterDrawables;
import com.remnants.game.sfx.ParticleEffectFactory;
import com.remnants.game.sfx.ShakeCamera;


public class BattleUI extends Window implements BattleObserver, CharacterDrawables {
    private static final String TAG = BattleUI.class.getSimpleName();

    //character sprites
    private AnimatedImage _enemyImage;
    private Image _tarenBattleSprite = new Image(/*_tarenBattleDrawable*/_abellaBattleDrawable);
    private Image _abellaBattleSprite = new Image(_abellaBattleDrawable);
    private Image _ipoBattleSprite = new Image(/*_ipoBattleDrawable*/_abellaBattleDrawable);
    private Image _tyrusBattleSprite = new Image(/*_tyrusBattleDrawable*/_abellaBattleDrawable);
    private Image _tarenWorldSprite = new Image(_tarenWorldDrawable);
    private Image _abellaWorldSprite = new Image(_abellaWorldDrawable);
    private Image _ipoWorldSprite = new Image(_ipoWorldDrawable);
    private Image _tyrusWorldSprite = new Image(_tyrusWorldDrawable);

    private final int _enemyWidth = 96;
    private final int _enemyHeight = 96;

    private BattleState _battleState = null;
    private TextButton _attackButton = null;
    private TextButton _runButton = null;
    private Label _damageValLabel = null;

    private float _battleTimer = 0;
    private final float _checkTimer = 1;

    private ShakeCamera _battleShakeCam = null;
    private Array<ParticleEffect> _effects;

    private float _origDamageValLabelY = 0;
    private Vector2 _currentImagePosition;

    public BattleUI(Stage stage){
        super("BATTLE", Utility.STATUSUI_SKIN, "solidbackground");

        float battleSpriteSize = stage.getHeight() * .23f;
        float buttonTableSizeScale = stage.getHeight() * .2f;

        _battleTimer = 0;
        _battleState = new BattleState();
        _battleState.addObserver(this);

        _effects = new Array<ParticleEffect>();
        _currentImagePosition = new Vector2(0,0);

        _damageValLabel = new Label("0", Utility.STATUSUI_SKIN);
        _damageValLabel.setVisible(false);

        _enemyImage = new AnimatedImage();
        _enemyImage.setTouchable(Touchable.disabled);

        _attackButton = new TextButton("Attack", Utility.STATUSUI_SKIN, "inventory");
        _runButton = new TextButton("Run", Utility.STATUSUI_SKIN, "inventory");

        //temporary image
        Image enemy1 = new Image(_enemyDrawable);
        Image enemy2 = new Image(_enemyDrawable);
        Image enemy3 = new Image(_enemyDrawable);

        float padding = battleSpriteSize * .45f;

        //Enemy table
        Table enemyTable = new Table();
        enemyTable.setDebug(true);
        enemyTable.add(enemy1).width(battleSpriteSize).height(battleSpriteSize).bottom().left();
        enemyTable.add(enemy2).width(battleSpriteSize).height(battleSpriteSize).bottom().padBottom(padding);
        enemyTable.add(enemy3).width(battleSpriteSize).height(battleSpriteSize).bottom().padBottom(padding * 2);

        //Battle Sprite table
        //TODO: split into two tables in order to stack images on top of one another with an offset
        Table bsTable = new Table();
        bsTable.setDebug(true);
        bsTable.add(_tarenBattleSprite).width(battleSpriteSize).height(battleSpriteSize).top().left();
        bsTable.add(_abellaBattleSprite).width(battleSpriteSize).height(battleSpriteSize).padTop(padding).top();
        bsTable.add(_ipoBattleSprite).width(battleSpriteSize).height(battleSpriteSize).padTop(padding * 2).top();
        bsTable.add(_tyrusBattleSprite).width(battleSpriteSize).height(battleSpriteSize).padTop(padding * 3).top();
        //Image rickroll = new Image(new TextureRegionDrawable(new TextureRegion(new Texture("sprites/characters/rick.jpg"))));
        //bsTable.add(rickroll);

        //dead zone between enemy's side and character's side
        float deadZoneWidth = stage.getWidth() - (battleSpriteSize * 3 + battleSpriteSize * 4);
        Gdx.app.log(TAG, "deadZoneWidth: " + deadZoneWidth + "      stage width: " + stage.getWidth() + "      enemy table width: " + battleSpriteSize * 3 + "      character table width: " + battleSpriteSize * 4);

        //buttons
        TextButton attackButton = new TextButton("Attack", Utility.STATUSUI_SKIN, "inventory");
        attackButton.getLabel().setFontScale(4);

        //button table
        Table buttonTable = new Table();
        buttonTable.setDebug(true);
        buttonTable.add(_tyrusWorldSprite).width(buttonTableSizeScale).height(buttonTableSizeScale).left();
        buttonTable.add(attackButton).height(buttonTableSizeScale).width(buttonTableSizeScale * 2);

        //layout table
        Table layoutTable = new Table();
        layoutTable.setDebug(true);
        layoutTable.add(enemyTable);
        layoutTable.add().width(deadZoneWidth);
        layoutTable.add(bsTable);
        layoutTable.row();
        layoutTable.add(buttonTable).left();

        //layout
        this.setStage(stage);
        this.setKeepWithinStage(true);
        this.setFillParent(true);
        this.setDebug(true);
        //this.add(_damageValLabel).align(Align.left).padLeft(_enemyWidth / 2).row();
        //this.add(_enemyImage).size(_enemyWidth, _enemyHeight).pad(10, 10, 10, _enemyWidth / 2);
        //this.add(table);
        this.add(layoutTable);

        //TODO: try adding this to the stage with stage.addActor() and see if it changes anything

        this.pack();

        _origDamageValLabelY = _damageValLabel.getY()+_enemyHeight;

        _attackButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        _battleState.playerAttacks();
                    }
                }
        );
        _runButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        _battleState.playerRuns();
                    }
                }
        );
    }

    public void battleZoneTriggered(int battleZoneValue){
        _battleState.setCurrentZoneLevel(battleZoneValue);
    }

    public boolean isBattleReady(){
        if( _battleTimer > _checkTimer ){
            _battleTimer = 0;
            return _battleState.isOpponentReady();
        }else{
            return false;
        }
    }

    public BattleState getCurrentState(){
        return _battleState;
    }

    @Override
    public void onNotify(Entity entity, BattleEvent event) {
        switch(event){
            case PLAYER_TURN_START:
                _runButton.setDisabled(true);
                _runButton.setTouchable(Touchable.disabled);
                _attackButton.setDisabled(true);
                _attackButton.setTouchable(Touchable.disabled);
                break;
            case OPPONENT_ADDED:
                _enemyImage.setEntity(entity);
                _enemyImage.setCurrentAnimation(Entity.AnimationType.IMMOBILE);
                _enemyImage.setSize(_enemyWidth, _enemyHeight);

                _currentImagePosition.set(_enemyImage.getX(),_enemyImage.getY());
                if( _battleShakeCam == null ){
                    _battleShakeCam = new ShakeCamera(_currentImagePosition.x, _currentImagePosition.y, 30.0f);
                }

                this.setTitle("Level " + _battleState.getCurrentZoneLevel() + " " + entity.getEntityConfig().getEntityID());
                break;
            case OPPONENT_HIT_DAMAGE:
                int damage = Integer.parseInt(entity.getEntityConfig().getPropertyValue(EntityConfig.EntityProperties.ENTITY_HIT_DAMAGE_TOTAL.toString()));
                _damageValLabel.setText(String.valueOf(damage));
                _damageValLabel.setY(_origDamageValLabelY);
                _battleShakeCam.startShaking();
                _damageValLabel.setVisible(true);
                break;
            case OPPONENT_DEFEATED:
                _damageValLabel.setVisible(false);
                _damageValLabel.setY(_origDamageValLabelY);
                break;
            case OPPONENT_TURN_DONE:
                 _attackButton.setDisabled(false);
                 _attackButton.setTouchable(Touchable.enabled);
                _runButton.setDisabled(false);
                _runButton.setTouchable(Touchable.enabled);
                break;
            case PLAYER_TURN_DONE:
                _battleState.opponentAttacks();
                break;
            case PLAYER_USED_MAGIC:
                float x = _currentImagePosition.x + (_enemyWidth/2);
                float y = _currentImagePosition.y + (_enemyHeight/2);
                _effects.add(ParticleEffectFactory.getParticleEffect(ParticleEffectFactory.ParticleEffectType.WAND_ATTACK, x,y));
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
            _enemyImage.setPosition(shakeCoords.x, shakeCoords.y);
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
