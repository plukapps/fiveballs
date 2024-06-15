package com.pluk.fiveballs.model;


/**
 * Representa una celda del tablero del juego. 
 * @author santilod
 *
 */
public class Cell {

	/**
	 * Indica la existencia de una bola
	 */
	private Ball ball;
	
	/**
	 * Coordenada que ocupa la bola en el tablero 
	 */
	private Coord coord;
	
	/**
	 * Crea una nueva celda vacia en la posicion <code>x</code> e <code>y</code>. 
	 * @param x
	 * @param y
	 */
	public Cell(int x, int y) {
		setBall(null);
		setCoord(new Coord(x, y));
	}

	/**
	 * Ball de la celda. 
	 * @return Devuelve la <code>ball</code> de la celda. En caso de que no contenga una, retorna 
	 * <code>null</code>.
	 */
	public Ball getBall() {
		return ball;
	}

	/**
	 * Setea la bola <code>ball</code> en la celda.
	 * @param ball
	 */
	public void setBall(Ball ball) {
		this.ball = ball;
	}

	/**
	 * Devuelve la coordenada <code>coord</code> de la celda.
	 * @return
	 */
	public Coord getCoord() {
		return coord;
	}

	/**
	 * Setea la coordenada <code>coord</code> a la celda.
	 * @param coord
	 */
	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	/**
	 * Checkea si la celda no contine una bola
	 * @return Retorna true si la celda no contiene una bola. 
	 */
	public boolean isFree() {
		if (ball == null) {
			return true;
		}
		return false;
	}
	
}