package com.pluk.fiveballs.model;

/**
 *	Representa un bola  
 *
 */
public class Ball {

	/**
	 * Color de la bola
	 */
	private BallColor ballColor;
	
	/**
	 * Se crea una bola a partir de un color
	 * @param color
	 */
	public Ball(BallColor color) {
		setBallColor(color);
	}

	public BallColor getBallColor() {
		return ballColor;
	}

	private void setBallColor(BallColor ballColor) {
		this.ballColor = ballColor;
	}
	
}
