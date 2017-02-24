package com.remnants.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;

import com.remnants.game.screens.CreditScreen;
import com.remnants.game.screens.CutSceneScreen;
import com.remnants.game.screens.GameMenuScreen;
import com.remnants.game.screens.GameOverScreen;
import com.remnants.game.screens.LoadGameScreen;
import com.remnants.game.screens.MainGameScreen;
import com.remnants.game.screens.MainMenuScreen;
import com.remnants.game.screens.NewGameScreen;


public class Remnants extends Game {

	private static MainGameScreen _mainGameScreen;
	private static MainMenuScreen _mainMenuScreen;
	private static LoadGameScreen _loadGameScreen;
	private static NewGameScreen _newGameScreen;
	private static GameOverScreen _gameOverScreen;
	private static CutSceneScreen _cutSceneScreen;
	private static CreditScreen _creditScreen;
	private static GameMenuScreen _gameMenuScreen;

	public static enum ScreenType{
		MainMenu,
		MainGame,
		LoadGame,
		NewGame,
		GameOver,
		WatchIntro,
		Credits,
		GameMenu
	}

	public Screen getScreenType(ScreenType screenType){
		switch(screenType){
			case MainMenu:
				return _mainMenuScreen;
			case MainGame:
				return _mainGameScreen;
			case LoadGame:
				return _loadGameScreen;
			case NewGame:
				return _newGameScreen;
			case GameOver:
				return _gameOverScreen;
			case WatchIntro:
				return _cutSceneScreen;
			case Credits:
				return _creditScreen;
			case GameMenu:
					return _gameMenuScreen;
			default:
				return _mainMenuScreen;
		}

	}

	@Override
	public void create(){
		_mainGameScreen = new MainGameScreen(this);
		_mainMenuScreen = new MainMenuScreen(this);
		_loadGameScreen = new LoadGameScreen(this);
		_newGameScreen = new NewGameScreen(this);
		_gameOverScreen = new GameOverScreen(this);
		_cutSceneScreen = new CutSceneScreen(this);
		_creditScreen = new CreditScreen(this);
		_gameMenuScreen = new GameMenuScreen(this);
		setScreen(_mainMenuScreen);
	}

	@Override
	public void dispose(){
		_mainGameScreen.dispose();
		_mainMenuScreen.dispose();
		_loadGameScreen.dispose();
		_newGameScreen.dispose();
		_gameOverScreen.dispose();
		_creditScreen.dispose();
		_gameMenuScreen.dispose();
	}

}
