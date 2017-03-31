package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.battle.LevelTable;

import sun.util.resources.be.CalendarData_be;

public class StatusUI extends Window implements StatusSubject {
    private static final String TAG = PlayerHUD.class.getSimpleName();

    private Image _hpBar;
    private Image _mpBar;
    private Image _xpBar;

    private Array<StatusObserver> _observers;

    private Array<LevelTable> _levelTables;
    private static final String LEVEL_TABLE_CONFIG = "scripts/level_tables.json";

    //Attributes
    private Stat _level;
    private Stat _gold;
    private Stat _xp;
    private Stat _hp;
    private Stat _mp;
    private Stat _mAtk;
    private Stat _pAtk;
    private Stat _def;
    private Stat _agl;

    private float _barWidth = 0;
    private float _barHeight = 0;

    TextButton debugLevelUp;

    public StatusUI(){
        super("", Utility.STATUSUI_SKIN);

        //initialize stats
        _level = new Stat("level");
        _gold = new Stat("gold");
        _xp = new Stat("xp");
        _hp = new Stat("hp");
        _mp = new Stat("mp");
        _mAtk = new Stat("mAtk");
        _pAtk = new Stat("pAtk");
        _def = new Stat("def");
        _agl = new Stat("agl");

        _levelTables = LevelTable.getLevelTables(LEVEL_TABLE_CONFIG);

        _observers = new Array<StatusObserver>();

        //images
        _hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
        _mpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"));
        _xpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"));

        //adjust height and width of bars
        //there should be a way to do this within the textureatlas
        _barWidth = _hpBar.getWidth() * 2;
        _barHeight = _hpBar.getHeight() * 5;
        _hpBar.setWidth(_barWidth);
        _hpBar.setHeight(_barHeight);
        _mpBar.setWidth(_barWidth);
        _mpBar.setHeight(_barHeight);
        _xpBar.setWidth(_barWidth);
        _xpBar.setHeight(_barHeight);

        //Align images
        _hpBar.setPosition(3, 6);
        _mpBar.setPosition(3, 6);
        _xpBar.setPosition(3, 6);

        //Add to layout
        defaults().expand().fill();

        //account for the title padding
        this.pad(this.getPadTop() + 10, 10, 5, 10);

        this.add(_hpBar).size(_barWidth, _barHeight).padRight(7).align(Align.left);
        this.add(_hp.getLabel());
        this.add(_hp.getValLabel()).align(Align.left);
        this.row();

        this.add(_mpBar).size(_barWidth, _barHeight).padRight(7).align(Align.left);
        this.add(_mp.getLabel());
        this.add(_mp.getValLabel()).align(Align.left);
        this.row();

        this.add(_xpBar).size(_barWidth, _barHeight).padRight(7).align(Align.left);
        this.add(_xp.getLabel());
        this.add(_xp.getValLabel()).align(Align.left).padRight(20);
        this.row();

        this.add(_mAtk.getLabel()).align(Align.left);
        this.add(_mAtk.getValLabel()).align(Align.left);
        this.row();
        this.add(_pAtk.getLabel()).align(Align.left);
        this.add(_pAtk.getValLabel()).align(Align.left);
        this.row();
        this.add(_def.getLabel()).align(Align.left);
        this.add(_def.getValLabel()).align(Align.left);
        this.row();
        this.add(_agl.getLabel()).align(Align.left);
        this.add(_agl.getValLabel()).align(Align.left);
        this.row();

        this.add(_level.getLabel()).align(Align.left);
        this.add(_level.getValLabel()).align(Align.left);
        this.row();
        this.add(_gold.getLabel());
        this.add(_gold.getValLabel()).align(Align.left);

        debugLevelUp = new TextButton("Lvl Up", Utility.STATUSUI_SKIN);
        debugLevelUp.getLabel().setFontScale(3);
        this.row();
        this.add(debugLevelUp).size(_barWidth * 1.5f, _barHeight);

        //this.debug();
        this.pack();

        debugLevelUp.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                int newLevel = _level.getValue() + 1;
                Gdx.app.log(TAG, "Setting stats for level " + newLevel);
                setStatusForLevel(newLevel);
                StatusUI.this.notify("", 0, StatusObserver.StatusEvent.LEVELED_UP);
            }
        });
    }

    public void updateToNewLevel(){
        for( LevelTable table: _levelTables ){
            //System.out.println("XPVAL " + _xpVal + " table XPMAX " + table.getXpMax() );
            if( _xp.getValue() > table.getXpMax() ){
                continue;
            }else{
                setXPValueMax(table.getXpMax());

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                notify(_level.getStatName(), _level.getValue(), StatusObserver.StatusEvent.LEVELED_UP);
                return;
            }
        }
    }

    public void setStatusForLevel(int level){
        for( LevelTable table: _levelTables ){
            if( Integer.parseInt(table.getLevelID()) == level ){
                setXPValueMax(table.getXpMax());
                setXPValue(0);

                setHPValueMax(table.getHpMax());
                setHPValue(table.getHpMax());

                setMPValueMax(table.getMpMax());
                setMPValue(table.getMpMax());

                //after debugging, only the ValueMax variables will be set
                setpAtkValueMax(table.getpAtkMax());
                setpAtkValue(table.getpAtkMax());

                setmAtkValueMax(table.getmAtkMax());
                setmAtkValue(table.getmAtkMax());

                setDefValueMax(table.getDefMax());
                setDefValue(table.getDefMax());

                setAglValueMax(table.getAglMax());
                setAglValue(table.getAglMax());

                setLevelValue(Integer.parseInt(table.getLevelID()));
                return;
            }
        }
    }

    //level
    public int getLevelValue(){
        return _level.getValue();
    }
    public void setLevelValue(int levelValue){
        _level.setValue(levelValue);
        notify(_level.getStatName(), _level.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }

    //gold
    public int getGoldValue(){
        return _gold.getValue();
    }
    public void setGoldValue(int goldValue){
        _gold.setValue(goldValue);
        notify(_gold.getStatName(), _gold.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addGoldValue(int goldValue){
        _gold.addValue(goldValue);
        notify(_gold.getStatName(), _gold.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }

    //XP
    public int getXPValue(){ return _xp.getValue(); }
    public void setXPValue(int xpValue){
        _xp.setValue(xpValue);

        if( _xp.getValue() > _xp.getMaxValue()){
            updateToNewLevel();
        }

        updateBar(_xpBar, _xp.getValue(), _xp.getMaxValue());

        notify(_xp.getStatName(), _xp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addXPValue(int xpValue){
        _xp.addValue(xpValue);

        if( _xp.getValue() > _xp.getMaxValue()){
            updateToNewLevel();
        }

        updateBar(_xpBar, _xp.getValue(), _xp.getMaxValue());

        notify(_xp.getStatName(), _xp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setXPValueMax(int maxXPValue){
        _xp.setMaxValue(maxXPValue);
    }
    public int getXPValueMax(){
        return _xp.getMaxValue();
    }

    //HP
    public int getHPValue(){
        return _hp.getValue();
    }
    public void removeHPValue(int hpValue){
        //get a negative value for hpValue
        hpValue -= hpValue * 2;
        _hp.addValue(hpValue);

        updateBar(_hpBar, _hp.getValue(), _hp.getMaxValue());

        notify(_hp.getStatName(), _hp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addHPValue(int hpValue){
        _hp.addValue(hpValue);

        updateBar(_hpBar, _hp.getValue(), _hp.getMaxValue());

        notify(_hp.getStatName(), _hp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setHPValue(int hpValue){
        _hp.addValue(hpValue);

        updateBar(_hpBar, _hp.getValue(), _hp.getMaxValue());

        notify(_hp.getStatName(), _hp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setHPValueMax(int maxHPValue){
        _hp.setMaxValue(maxHPValue);
    }
    public int getHPValueMax(){
        return _hp.getMaxValue();
    }

    //MP
    public int getMPValue(){
        return _mp.getValue();
    }
    public void removeMPValue(int mpValue){
        //get a negative value for mpValue
        mpValue -= mpValue * 2;
        _mp.addValue(mpValue);
        updateBar(_mpBar, _mp.getValue(), _mp.getMaxValue());
        notify(_mp.getStatName(), _mp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addMPValue(int mpValue){
        _mp.addValue(mpValue);
        updateBar(_mpBar, _mp.getValue(), _mp.getMaxValue());
        notify(_mp.getStatName(), _mp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setMPValue(int mpValue){
        _mp.addValue(mpValue);
        updateBar(_mpBar, _mp.getValue(), _mp.getMaxValue());
        notify(_mp.getStatName(), _mp.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setMPValueMax(int maxmpValue){
        _mp.setMaxValue(maxmpValue);
    }
    public int getMPValueMax(){
        return _mp.getMaxValue();
    }

    public void updateBar(Image bar, int currentVal, int maxVal){
        int val = MathUtils.clamp(currentVal, 0, maxVal);
        float tempPercent = (float) val / (float) maxVal;
        float percentage = MathUtils.clamp(tempPercent, 0, 100);
        bar.setSize(_barWidth*percentage, _barHeight);
    }

    //magic attack
    public int getmAtkValue(){
        return _mAtk.getValue();
    }
    public void removemAtkValue(int mAtkValue){
        //get a negative value for mAtkValue
        mAtkValue -= mAtkValue * 2;
        _mAtk.addValue(mAtkValue);
        notify(_mAtk.getStatName(), _mAtk.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addmAtkValue(int mAtkValue){
        _mAtk.addValue(mAtkValue);
        notify(_mAtk.getStatName(), _mAtk.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setmAtkValue(int mAtkValue){
        _mAtk.addValue(mAtkValue);
        notify(_mAtk.getStatName(), _mAtk.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setmAtkValueMax(int maxmAtkValue){
        _mAtk.setMaxValue(maxmAtkValue);
    }
    public int getmAtkValueMax(){
        return _mAtk.getMaxValue();
    }

    //physical attack
    public int getpAtkValue(){
        return _pAtk.getValue();
    }
    public void removepAtkValue(int pAtkValue){
        //get a negative value for pAtkValue
        pAtkValue -= pAtkValue * 2;
        _pAtk.addValue(pAtkValue);
        notify(_pAtk.getStatName(), _pAtk.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addpAtkValue(int pAtkValue){
        _pAtk.addValue(pAtkValue);
        notify(_pAtk.getStatName(), _pAtk.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setpAtkValue(int pAtkValue){
        _pAtk.addValue(pAtkValue);
        notify(_pAtk.getStatName(), _pAtk.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setpAtkValueMax(int maxpAtkValue){
        _pAtk.setMaxValue(maxpAtkValue);
    }
    public int getpAtkValueMax(){
        return _pAtk.getMaxValue();
    }

    //defense
    public int getDefValue(){
        return _def.getValue();
    }
    public void removeDefValue(int defValue){
        //get a negative value for defValue
        defValue -= defValue * 2;
        _def.addValue(defValue);
        notify(_def.getStatName(), _def.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addDefValue(int defValue){
        _def.addValue(defValue);
        notify(_def.getStatName(), _def.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setDefValue(int defValue){
        _def.addValue(defValue);
        notify(_def.getStatName(), _def.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setDefValueMax(int maxDefValue){
        _def.setMaxValue(maxDefValue);
    }
    public int getDefValueMax(){
        return _def.getMaxValue();
    }

    //agility
    public int getAglValue(){
        return _agl.getValue();
    }
    public void removeAglValue(int aglValue){
        //get a negative value for aglValue
        aglValue -= aglValue * 2;
        _agl.addValue(aglValue);
        notify(_agl.getStatName(), _agl.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void addAglValue(int aglValue){
        _agl.addValue(aglValue);
        notify(_agl.getStatName(), _agl.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setAglValue(int aglValue){
        _agl.addValue(aglValue);
        notify(_agl.getStatName(), _agl.getValue(), StatusObserver.StatusEvent.UPDATED_STAT);
    }
    public void setAglValueMax(int maxAglValue){
        _agl.setMaxValue(maxAglValue);
    }
    public int getAglValueMax(){
        return _agl.getMaxValue();
    }

    @Override
    public void addObserver(StatusObserver statusObserver) {
        _observers.add(statusObserver);
    }

    @Override
    public void removeObserver(StatusObserver statusObserver) {
        _observers.removeValue(statusObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(StatusObserver observer: _observers){
            _observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(String name, int value, StatusObserver.StatusEvent event) {
        for(StatusObserver observer: _observers){
            observer.onNotify(name, value, event);
        }
    }
}
