package com.pluk.fiveballs.handlers;

public class FactoryGame {

	public static IGameManager newGame() {
		return new GameManager();
	}
	
	public FactoryGame(){
	}

	public static IGameManager restart() {
		return newGame();
	}
	
}
