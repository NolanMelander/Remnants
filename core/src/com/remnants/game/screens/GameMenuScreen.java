package com.remnants.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.remnants.game.Remnants;
import com.remnants.game.UI.GameMenuUI;

/**
 * Created by brian on 2/23/2017.
 */

public class GameMenuScreen extends GameScreen {
    private Stage _stage;
    private Remnants _game;
    private GameMenuUI _menuUI;

    public GameMenuScreen(Remnants game) {
        //initial creation
        _game = game;
        _stage = new Stage();
        _menuUI = new GameMenuUI(game);
    }


    @Override
    public void render(float delta) {
        if (delta == 0)
            return;

        _menuUI.render(delta);

    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(_stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        _stage.clear();
        _stage.dispose();
    }
}