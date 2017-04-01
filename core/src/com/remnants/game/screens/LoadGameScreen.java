package com.remnants.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Remnants;
import com.remnants.game.Utility;
import com.remnants.game.audio.AudioObserver;
import com.remnants.game.profile.ProfileManager;


public class LoadGameScreen extends GameScreen {
    private Stage _stage;
	private Remnants _game;
	private List _listItems;

	public LoadGameScreen(Remnants game){
		_game = game;

		//create
		_stage = new Stage();

		TextButton loadButton = new TextButton("Load", Utility.STATUSUI_SKIN);
		TextButton backButton = new TextButton("Back",Utility.STATUSUI_SKIN);
		loadButton.getLabel().setFontScale(6);
		backButton.getLabel().setFontScale(6);

		ProfileManager.getInstance().storeAllProfiles();
		_listItems = new List(Utility.STATUSUI_SKIN, "inventory");
		Array<String> list = ProfileManager.getInstance().getProfileList();
		_listItems.setItems(list);
        //_listItems.setDebug(true);
        _listItems.getStyle().font.getData().setScale(4);
		ScrollPane scrollPane = new ScrollPane(_listItems);

        //scrollPane.setDebug(true);
		scrollPane.setOverscroll(false, false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setScrollbarsOnTop(true);

		Table table = new Table();
		Table bottomTable = new Table();

		//Layout
        //table.setDebug(true);
		table.center();
		table.setFillParent(true);
		table.padBottom(loadButton.getHeight());
		table.add(scrollPane).center().width(_stage.getWidth() / 3).height(_stage.getHeight() / 3);

        //bottomTable.setDebug(true);
		bottomTable.setHeight(loadButton.getHeight() * 2);
		bottomTable.setWidth(Gdx.graphics.getWidth());
		bottomTable.center();
		bottomTable.add(loadButton).padRight(50);
		bottomTable.add(backButton);
		bottomTable.padBottom(loadButton.getHeight());

		_stage.addActor(table);
		_stage.addActor(bottomTable);

		Gdx.app.log("LoadGameScreen","creating click listeners");

		//Listeners
		backButton.addListener(new ClickListener() {
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

		loadButton.addListener(new ClickListener() {
								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   if( _listItems.getSelected() == null ) return;
									   String fileName = _listItems.getSelected().toString();
									   if (fileName != null && !fileName.isEmpty()) {
										   FileHandle file = ProfileManager.getInstance().getProfileFile(fileName);
										   if (file != null) {
											   ProfileManager.getInstance().setCurrentProfile(fileName);
											   LoadGameScreen.this.notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
											   _game.setScreen(_game.getScreenType(Remnants.ScreenType.MainGame));
										   }
									   }
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
		Array<String> list = ProfileManager.getInstance().getProfileList();
		_listItems.setItems(list);
		Gdx.input.setInputProcessor(_stage);
		Gdx.app.log("LoadGameScreen","set input processor");
	}

	@Override
	public void hide() { /*Gdx.input.setInputProcessor(null);*/	}

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
