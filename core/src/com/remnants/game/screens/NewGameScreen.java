package com.remnants.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.remnants.game.Remnants.ScreenType;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.profile.ProfileManager;

public class NewGameScreen extends GameScreen {

	private Stage _stage;
	private Remnants _game;
	private TextField _profileText;
	private Dialog _overwriteDialog;

	public NewGameScreen(Remnants game){
		_game = game;

		//create
		_stage = new Stage();

		Label profileName = new Label("Enter Profile Name: ", Utility.STATUSUI_SKIN);
		profileName.setFontScale(4.5f);
		_profileText  = new TextField("",Utility.STATUSUI_SKIN, "inventory");
		_profileText.setMaxLength(20);

		_overwriteDialog = new Dialog("Overwrite?", Utility.STATUSUI_SKIN, "solidbackground");
		Label overwriteLabel = new Label("Overwrite existing profile name?", Utility.STATUSUI_SKIN);
		overwriteLabel.setFontScale(3);
		TextButton cancelButton = new TextButton("Cancel", Utility.STATUSUI_SKIN, "inventory");
		cancelButton.getLabel().setFontScale(6);

		TextButton overwriteButton = new TextButton("Overwrite", Utility.STATUSUI_SKIN, "inventory");
		overwriteButton.getLabel().setFontScale(6);
		_overwriteDialog.setKeepWithinStage(true);
		_overwriteDialog.setModal(true);
		_overwriteDialog.setMovable(false);
		_overwriteDialog.text(overwriteLabel);

		TextButton startButton = new TextButton("Start", Utility.STATUSUI_SKIN);
		TextButton backButton = new TextButton("Back", Utility.STATUSUI_SKIN);
		startButton.getLabel().setFontScale(6);
		backButton.getLabel().setFontScale(6);

		//Layout
		_overwriteDialog.row();
		_overwriteDialog.button(overwriteButton).bottom().left();
		_overwriteDialog.button(cancelButton).bottom().right();

		Table topTable = new Table();
		//topTable.setDebug(true);
		topTable.setFillParent(true);
		topTable.add(profileName).center().padBottom(25).row();
		topTable.add(_profileText).center().width(_stage.getWidth() / 3).height(profileName.getPrefHeight());

		Table bottomTable = new Table();
		//bottomTable.setDebug(true);
		bottomTable.setHeight(startButton.getHeight());
		bottomTable.setWidth(Gdx.graphics.getWidth());
		bottomTable.center();
		bottomTable.add(startButton).padRight(50).padBottom(100);
		bottomTable.add(backButton).padBottom(100);

		_stage.addActor(topTable);
		_stage.addActor(bottomTable);

		//Listeners
		cancelButton.addListener(new ClickListener() {

									 @Override
									 public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
										 return true;
									 }

									 @Override
									 public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
										 _overwriteDialog.hide();
									 }
								 }
		);

		overwriteButton.addListener(new ClickListener() {

										@Override
										public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
											return true;
										}

										@Override
										public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
											String messageText = _profileText.getText();
											ProfileManager.getInstance().writeProfileToStorage(messageText, "", true);
											ProfileManager.getInstance().setCurrentProfile(messageText);
											ProfileManager.getInstance().setIsNewProfile(true);
											_overwriteDialog.hide();
											NewGameScreen.this.notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
											_game.setScreen(_game.getScreenType(ScreenType.MainGame));
										}

									}
		);

		startButton.addListener(new ClickListener() {

									@Override
									public boolean touchDown(InputEvent event, float x, float y, int pointer, int button ){
										return true;
									}

									@Override
									public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
										String messageText = _profileText.getText();
										//check to see if the current profile matches one that already exists
										boolean exists = false;

										exists = ProfileManager.getInstance().doesProfileExist(messageText);

										if( exists ){
											//Pop up dialog for Overwrite
											_overwriteDialog.show(_stage);
										}else{
											ProfileManager.getInstance().writeProfileToStorage(messageText,"",false);
											ProfileManager.getInstance().setCurrentProfile(messageText);
											ProfileManager.getInstance().setIsNewProfile(true);
											NewGameScreen.this.notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
											_game.setScreen(_game.getScreenType(ScreenType.MainGame));
										}
									}
								}
		);

		backButton.addListener(new ClickListener() {

								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   _game.setScreen(_game.getScreenType(ScreenType.MainMenu));
								   }
							   }
		);

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
		_overwriteDialog.hide();
		_profileText.setText("");
		Gdx.input.setInputProcessor(_stage);
	}

	@Override
	public void hide() {
		_overwriteDialog.hide();
		_profileText.setText("");
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
