package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;


/**
 * Created by brian on 2/17/2017.
 */

public class dPadUI {

    private Table _table;
    private Skin _skin;
    private Touchpad.TouchpadStyle _style;
    private Drawable _touchpadBackground;
    private Drawable _touchpadKnob;
    private WidgetGroup _group = new WidgetGroup();
    private Array<dPadObserver> _observers;

    private Touchpad _touchpad;

    public dPadUI() {
        _observers = new Array<dPadObserver>();

        _skin = new Skin();
        _skin.add("touchpad-background", new Texture("skins/pad-background.png"));
        _skin.add("touchpad-knob", new Texture("skins/pad-knob.png"));
        _style = new Touchpad.TouchpadStyle();
        _touchpadBackground = _skin.getDrawable("touchpad-background");
        _touchpadKnob = _skin.getDrawable("touchpad-knob");
        _style.background = _touchpadBackground;
        _style.knob = _touchpadKnob;
        _touchpad = new Touchpad(10, _style);
        _touchpad.setBounds(15,15,200,200);

        _table = new Table();
        _table.add(_touchpad);

        _group.addActor(_table);
    }

    public void setVisible(boolean vis) {
        _touchpad.setVisible(vis);
    }


    public WidgetGroup getGroup() { return _group;}
    public Touchpad.TouchpadStyle getStyle() { return _style; }
    public Touchpad getTouchpad() { return _touchpad; }

    public boolean isUp() {
        if (_touchpad.getKnobPercentY() > .8)
            return true;
        else
            return false;
    }

    public boolean isDown() {
        if (_touchpad.getKnobPercentY() < -.8)
            return true;
        else
            return false;
    }

    public boolean isLeft() {
        if (_touchpad.getKnobPercentX() < -.8)
            return true;
        else
            return false;
    }

    public boolean isRight() {
        if (_touchpad.getKnobPercentX() > .8)
            return true;
        else
            return false;
    }

    //Observer methods
    public void addObserver(dPadObserver padObserver) {
        _observers.add(padObserver);
    }

    public void removeObserver(dPadObserver padObserver) {
        _observers.removeValue(padObserver, true);
    }

    public void removeAllObservers() {
        for(dPadObserver observer: _observers){
            _observers.removeValue(observer, true);
        }
    }

    public void notify(int value, dPadObserver.dPadEvent event) {
        for(dPadObserver observer: _observers){
            observer.onNotify(value, event);
        }
    }

}
