package com.pluk.fiveballs.model;

import java.io.Serializable;
import java.util.LinkedList;


public class Coord implements Serializable, Comparable<Coord> {

	private static final long serialVersionUID = 5668437993441275584L;

	private int x;
	private int y;
	
	/**
	 * Se crea una nueva coordenada (x,y)
	 * @param x Coordenda en el eje x
	 * @param y Coordenda en el eje y
	 * @throws IllegalArgumentException Ocurren una excepcion cuando las coordenadas no pertenecen 
	 * a el tablero
	 */
	public Coord(int x, int y) throws IllegalArgumentException{
		if (!assertCoord(x, y)) {
			String message = "La corrdenada es incorrecta: (" + x + "," + y +")";
			throw new IllegalArgumentException(message);
		}
		setX(x);
		setY(y);
	}
	
	/**
	 * Constructor por copia
	 * @param coord
	 */
	public Coord(Coord coord) {
		this.x = coord.getX();
		this.y = coord.getY();
	}

	public int getX() {
		return x;
	}
	
	private void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	private void setY(int y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		Coord coord = (Coord) o;
		return (this.x == coord.getX() && this.y == coord.getY());
	}
	
	public LinkedList<Coord> nearCoordsSimple() {
		LinkedList<Coord> coords = new LinkedList<Coord>();
		addUpCoord(coords);
		addRightCoord(coords);
		addDownCoord(coords);
		addLeftCoord(coords);
		return coords;
	}
	
	/**
	 * Busca las coordenadas que se encuentra arrbia, abajo, a la derecha y a la izquierda de una 
	 * coordenada. En el caso de las coordenadas que se encuentran en el borde del tablero no se 
	 * toman en cuenta. 
	 * @param end 
	 * @return coords
	 */
	public LinkedList<Coord> nearCoordsComplex(Coord end) {

		if (this.equals(end)) {
			return new LinkedList<Coord>();
		}
		
		// Result
		LinkedList<Coord> coords = new LinkedList<Coord>();
		
		if (this.x == end.getX()) {
			if (this.y < end.getY()) {
				addRightCoord(coords);
				addUpCoord(coords);
				addDownCoord(coords);
				addLeftCoord(coords);
			} else {
				addLeftCoord(coords);
				addUpCoord(coords);
				addDownCoord(coords);
				addRightCoord(coords);
			}
		} else if (this.x > end.getX()) {
			addUpCoord(coords);
			if (this.y < end.getY()) {
				addRightCoord(coords);
				addLeftCoord(coords);
			} else {
				addLeftCoord(coords);
				addRightCoord(coords);
			}
			addDownCoord(coords);
		} else {
			addDownCoord(coords);
			if (this.y < end.getY()) {
				addRightCoord(coords);
				addLeftCoord(coords);
			} else {
				addLeftCoord(coords);
				addRightCoord(coords);
			}
			addUpCoord(coords);
		}

		return coords;
	}

	private void addDownCoord(LinkedList<Coord> coords) {
		// Coordenada de abajo
		if (assertCoord(x+1, y)) {
			coords.add(new Coord(x+1, y));
		}
	}

	private void addUpCoord(LinkedList<Coord> coords) {
		// Coordenada de arriba 
		if (assertCoord(x-1, y)) {
			coords.add(new Coord(x-1, y));
		}
	}

	private void addLeftCoord(LinkedList<Coord> coords) {
		// Coordenada de la izquierda
		if (assertCoord(x, y-1)) {
			coords.add(new Coord(x, y-1));
		}
	}

	private void addRightCoord(LinkedList<Coord> coords) {
		// Coordenada de la derecha
		if (assertCoord(x, y+1)) {
			coords.add(new Coord(x, y+1));
		}
	}
	
	/**
	 * Comprueba que una coordenada es factible en los puntos x e y.
	 * @param x
	 * @param y
	 * @return Devuelve true si la coordenada (x,y) es factible.
	 */
	private boolean assertCoord(int x, int y) {
		if (x < 0 || x >= Consts.game.gridsize || 
			y < 0 || y >= Consts.game.gridsize) {
			return false;
		}
		return true;
	}

	/**
	 * Busca la coordenada mas proxima que se encuentra en la direccion <code>dir</code> de la 
	 * coordenada actual
	 * @param dir
	 * @return Retorna una nueva coordenada, a partir de la direccion <code>dir</code>. Si no existe 
	 * se retorna null.
	 */
	public Coord getNextCoord(Direction dir) {
		switch (dir) {
			case SOUTH:
				return assertCoord(x+1, y) ? new Coord(x+1, y) : null;
			case NORTH: 
				return assertCoord(x-1, y) ? new Coord(x-1, y) : null;
			case EAST: 
				return assertCoord(x, y+1) ? new Coord(x, y+1) : null;
			case WEST: 
				return assertCoord(x, y-1) ? new Coord(x, y-1) : null;
			case SOUTHEAST: 
				return assertCoord(x+1, y+1) ? new Coord(x+1, y+1) : null;
			case SOUTHWEST: 
				return assertCoord(x+1, y-1) ? new Coord(x+1, y-1) : null;
			case NORTHESAT: 
				return assertCoord(x-1, y+1) ? new Coord(x-1, y+1) : null;
			case NORTHWEST:
				return assertCoord(x-1, y-1) ? new Coord(x-1, y-1) : null;
			default : return null; 
		}
	}

	@Override
	public String toString() {
		return "("+ x + "," + y +")";
	}

	public int compareTo(Coord another) {
		if (this.x < another.getX()) {
			return -1;
		} 
		if (this.x > another.getX()) {
			return 1;
		} 
		if (this.y < another.getY()) {
			return -1;
		}
		if (this.y > another.getY()) {
			return 1;
		}
		return 0;
	}
	
	
}
