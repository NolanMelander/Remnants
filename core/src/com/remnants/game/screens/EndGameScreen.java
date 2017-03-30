package com.remnants.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioObserver;

/**
 * Created by main on 3/30/17.
 */

public class EndGameScreen extends GameScreen {
    private static final String TAG = EndGameScreen.class.getSimpleName();

    private Stage _stage;
    private Remnants _game;
    private static final String END_MESSAGE = "It isn't over yet. Drachonias will continue his search for the other Remnants.";

    public EndGameScreen(Remnants game) {
        _game = game;

        _stage = new Stage();
        float buttonWidth = _stage.getWidth() / 4;
        float buttonHeight = _stage.getHeight() / 6;

        Label messageLabel = new Label(END_MESSAGE, Utility.STATUSUI_SKIN);
        messageLabel.setFontScale(5);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        TextButton mainMenuButton = new TextButton("Main Menu", Utility.STATUSUI_SKIN);
        TextButton creditsButton = new TextButton("Credits", Utility.STATUSUI_SKIN);
        mainMenuButton.getLabel().setFontScale(4);
        creditsButton.getLabel().setFontScale(4);

        Table table = new Table();
        //table.setDebug(true);
        table.add(messageLabel).width(_stage.getWidth()).align(Align.center).padBottom(100).row();
        table.add(mainMenuButton).width(buttonWidth).height(buttonHeight).row();
        table.add(creditsButton).width(buttonWidth).height(buttonHeight);
        table.setPosition(_stage.getWidth() / 2, _stage.getHeight() / 2);

        _stage.addActor(table);

        mainMenuButton.addListener(new ClickListener() {

                                       @Override
                                       public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                           return true;
                                       }

                                       @Override
                                       public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                                           _game.setScreen(_game.getScreenType(Remnants.ScreenType.MainMenu));
                                       }
                                   }
        );

        notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
    }

    @Override
    public void render(float delta) {
        if( delta == 0){
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(_stage);
        notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        _stage.clear();
        _stage.dispose();
    }
}
