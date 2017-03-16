package com.remnants.game.UI;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.remnants.game.Utility;

/**
 * Created by main on 3/16/17.
 */

public class Stat {

    private String _statName;
    private Label _label;
    private Label _valLabel;
    private int _value;
    private int _maxValue;

    public Stat(String name) {
        _statName = name;
        _label = new Label(_statName + ":", Utility.STATUSUI_SKIN);
        _value = 0;
        _maxValue = 0;
        _valLabel = new Label(String.valueOf(_value), Utility.STATUSUI_SKIN);

        //set font scales
        _label.setFontScale(3);
        _valLabel.setFontScale(3);
    }

    //value
    public int getValue() { return _value; }
    public void setValue(int value) {
        this._value = value;
        _valLabel.setText(String.valueOf(value));
    }
    public void addValue(int value) {
        _value = MathUtils.clamp(_value + value, 0, _maxValue);
        _valLabel.setText(String.valueOf(_value));
    }

    //stat name
    public String getStatName() { return _statName; }
    public void setStatName(String name) { _statName = name; }

    //label
    public Label getLabel() { return _label; }
    public void setLabel(Label label) { this._label = label; }

    //value label
    public Label getValLabel() { return _valLabel; }
    public void setValLabel(Label valLabel) { this._valLabel = valLabel; }

    //max value
    public int getMaxValue() { return _maxValue; }
    public void setMaxValue(int _maxValue) { this._maxValue = _maxValue; }
}
