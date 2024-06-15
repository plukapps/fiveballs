package com.pluk.fiveballs.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import android.util.Log;

import com.pluk.fiveballs.exceptions.AlreadySelectedBallExeption;
import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotEmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotExistEmptyCellException;
import com.pluk.fiveballs.exceptions.UnSelectedBallExpetion;
import com.pluk.fiveballs.model.BallColor;
import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.model.Coord;
import com.pluk.fiveballs.model.Grid;

/**
 * Representa la funcionalidades del juego.
 * @author santilod
 *
 */
public class GameManager implements IGameManager {

	private static final String TAG = "GameManager";

	/**
	 * Score
	 */
	private int score;

	/**
	 * Panel del juego
	 */
	private Grid grid;

	/**
	 * Contiene las bolas que estan en linea.
	 */
	private LinkedList<Coord> coordsInLine;

	/**
	 * Contiene los colores de las sguientes bolas.
	 */
	private LinkedList<BallColor> nextColorBalls;

	public GameManager() {
		this.score = 0;
		this.grid = new Grid();
		this.coordsInLine = new LinkedList<Coord>();
		this.nextColorBalls = new LinkedList<BallColor>();
	}

	public HashMap<Coord, BallColor> addBalls(LinkedList<BallColor> balls) throws NotEmptyCellExeption, 
	EmptyCellExeption, NotExistEmptyCellException {

		HashMap<Coord, BallColor> newBalls = new HashMap<Coord, BallColor>();

		coordsInLine.clear();

		for (BallColor ballColor : balls) {

			if (grid.getCountFree() == 0) {
				break;
			}

			// Obtengo una coordenada aleatoria
			Coord coord = grid.getRandomCoord();

			// Agrego una nueva bola en la coordenada coord
			grid.addBall(ballColor, coord);

			// Guardo para el result
			newBalls.put(coord, ballColor);

			// Obtengo las que estan en linea

			for (Coord coordInLine : grid.getBallsInLine(coord)) {
				if (!coordsInLine.contains(coordInLine)) {
					coordsInLine.add(coordInLine);
				}
			}

			// Borro las que estan en linea
			if (coordsInLine.size() > 0) {
				// Actualizo el score
				score +=  getLinesScore(coordsInLine.size());
			}
		}

		// Devuelvo las nuevas coordenadas
		return newBalls;
	}

	/**
	 * 	obtener el puntaje obtenido al crear 
	 * 	linea/s de 5 o mas bolas
	 */
	private int getLinesScore(int balls) {
		int score = 0;
		if (balls > 6) {
			//funcion aproximada al puntaje de fivemore de ubuntu

			//si balls < 10 f(x) = x2 -3*x - 10
			if (balls < 10) {
				score = balls*balls - 3*balls - 10;

			} else { //si balls >= 10 f(x) = x2 -3*x + 20
				score = balls*balls;
			}
		} else {
			score = balls * 2;
		}
		return score;
	}

	public LinkedList<Coord> getCoordsInLine() {
		Collections.sort(this.coordsInLine);
		return this.coordsInLine;
	}

	public LinkedList<BallColor> getNextColorBalls() {

		// Limpio las proximas bolas
		this.nextColorBalls.clear();

		for (int i = 0; i < Consts.game.countNextBalls; i++) {
			BallColor ballColor = grid.getRandomBallColor();
			nextColorBalls.add(ballColor);	
		}

		return this.nextColorBalls;
	}

	public LinkedList<BallColor> getCurrentNextBalls() {
		return this.nextColorBalls;
	}

	public int getScore() {
		return this.score;
	}

	public Coord getSelected() {
		return grid.getSelected();
	}

	public BallColor getSelectedColor() throws UnSelectedBallExpetion, EmptyCellExeption {
		return grid.getSelectedColor();
	}

	public boolean hasBall(Coord coord) {
		return grid.hasBall(coord);
	}
	
	public BallColor getBallColor(Coord coord) throws EmptyCellExeption {
		return grid.getBall(coord).getBallColor();
	}

	public boolean hasBallsInLine() {
		return this.coordsInLine.size() >= Consts.game.inlinemin;
	}

	public boolean hasSelected() {
		return grid.hasSelected();
	}

	public LinkedList<Coord> moveTo(Coord coord) throws UnSelectedBallExpetion, EmptyCellExeption, 
	NotEmptyCellExeption {

		// No es posible tener mover una bola si no existe una seleccionada
		if (!grid.hasSelected()) {
			throw new UnSelectedBallExpetion();
		}

		if (grid.hasPathTo(coord)) {

			// Como hay camino entre el inicio y el fin, muevo la bola.
			grid.moveTo(coord);

			coordsInLine.clear();
			coordsInLine = grid.getBallsInLine(coord);
			score += getLinesScore(coordsInLine.size());

		} else {
			grid.getPath().clear();
		}

//		LogPath();

		return grid.getPath();
	}

	public void removeBalls() throws EmptyCellExeption {
		this.grid.removeBalls(coordsInLine);
	}

	public BallColor select(Coord coord) throws AlreadySelectedBallExeption, EmptyCellExeption {
		if (grid.hasSelected()) {
			throw new AlreadySelectedBallExeption(grid.getSelected());
		}

		return grid.selectBall(coord);
	}

	public void unselect() throws UnSelectedBallExpetion {
		grid.unselect();
	}

	public LinkedList<Coord> freeCells() {
		return grid.getFreeCells();
	}

	public String getPanelGrid() {
		StringBuilder builder = new StringBuilder(); 

		builder.append("__________________________________________________\n");

		builder.append("Next Balls: ");

		for (BallColor ballColor : this.nextColorBalls) {
			char c = BallColor.getBallColorChar(ballColor);
			builder.append(c);
			builder.append("  ");
		}

		builder.append("\n");
		builder.append(grid.toString());

		return builder.toString();
	}

	void LogPath() {
		LinkedList<Coord> path = grid.getPath();
		StringBuilder builder = new StringBuilder();
		for (Coord coord : path) {
			builder.append(coord.toString());
		}
		Log.i(TAG, builder.toString());
	}

	public boolean emptyGrid() {
		return this.grid.emptyGrid();
	}

}
