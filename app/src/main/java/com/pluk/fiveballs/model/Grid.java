package com.pluk.fiveballs.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import android.util.Log;

import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotEmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotExistEmptyCellException;
import com.pluk.fiveballs.exceptions.UnSelectedBallExpetion;

/**
 * Representa el tablero.
 * 
 * @author santilod
 * 
 */
public class Grid {

	private static final String TAG = "grid";

	/**
	 * Celdas del tablero
	 */
	private Cell cells[][];

	/**
	 * Marca la coordenad de la celda como seleccionada.
	 */
	private Coord selected;

	/**
	 * Lista con las celda libres.
	 */
	private LinkedList<Coord> freeCells;

	private LinkedList<Coord> path;

	private Random randomCell;

	private Random randomColor;

	/**
	 * Crea el tablero, con celdas vacias
	 */
	public Grid() {

		this.cells = new Cell[Consts.game.gridsize][Consts.game.gridsize];
		this.freeCells = new LinkedList<Coord>();
		setPath(new LinkedList<Coord>()); // Creo el camino vacio.
		this.selected = null;

		randomCell = new Random();
		Long time = new Date().getTime();
		randomCell.setSeed(time);

		randomColor = new Random();
		time = new Date().getTime();
		randomColor.setSeed(time);

		for (int i = 0; i < Consts.game.gridsize; i++) {
			for (int j = 0; j < Consts.game.gridsize; j++) {

				// Se crea la celda (i, j)
				Cell cell = new Cell(i, j);
				cells[i][j] = cell;

				// Se la argega como celda libre
				freeCells.add(cell.getCoord());
			}
		}
	}

	/**
	 * Agrega una nueva bola al tablero
	 * 
	 * @param ballColor
	 *            Color de la bola
	 * @param coord
	 *            Coordenada de la celda que se ingresara la nueva bola
	 * @throws NotEmptyCellExeption
	 *             Si la celda de la posicion <code>coord</code> no esta vacia.
	 */
	public void addBall(BallColor ballColor, Coord coord)
			throws NotEmptyCellExeption {

		Cell cell = getCell(coord);
		if (!cell.isFree()) {
			throw new NotEmptyCellExeption(coord);
		}

		Ball ball = new Ball(ballColor);
		cell.setBall(ball);

		freeCells.remove(coord);
	}

	private LinkedList<Coord> getAvialbleNearCoords(Coord coord, Coord end,
			int[][] visited) {
		// Tomo las vecinas.
		LinkedList<Coord> nearCoords = coord.nearCoordsComplex(end);

		// Ademas me quedo solo con las que estan libres.
		nearCoords.retainAll(freeCells);

		return nearCoords;
	}

	/**
	 * Obtiene la bola que se encuentra en la posicion <code>coord</code>
	 * 
	 * @param coord
	 *            de la celdas donde se encuetra la bola
	 * @return Retorna la <code>ball</code> de las posicion <code>coord</code>
	 * @throws EmptyCellExeption
	 *             Si la celda de la posicion <code>coord</code> esta vacia.
	 */
	public Ball getBall(Coord coord) throws EmptyCellExeption {

		Cell cell = getCell(coord);
		if (cell.isFree()) {
			throw new EmptyCellExeption(coord);
		}

		return cell.getBall();
	}

	/**
	 * Devuelve las coordenadas de las bolas que se encuetran en linea, despues
	 * que una bola se mueva a la coordenada <code>moved</code>.
	 * 
	 * @param moved
	 * @return Retorna una collecion de coordenadas que se encuentran en linea,
	 *         y deben borrarse del tablero.
	 * @throws EmptyCellExeption
	 *             Si la celda de la cooredenada <code>moved</code> no contiene
	 *             alguna bola.
	 */
	public LinkedList<Coord> getBallsInLine(Coord moved)
			throws EmptyCellExeption {

		Cell cell = getCell(moved);
		if (cell.isFree()) {
			throw new EmptyCellExeption(moved);
		}


		// Tomo la bola de la coordenada moved
		Ball ball = getBall(moved);

		// Color de la bola.
		BallColor ballColor = ball.getBallColor();

		LinkedList<Coord> coordsInLine = null;

		if (ballColor != BallColor.WILDCARD) {
			coordsInLine = getBallsInLineImpl(moved, ballColor);
			return coordsInLine;
		} else {

			LinkedList<Coord> listR = getBallsInLineImpl(moved, BallColor.RED);
			LinkedList<Coord> listY = getBallsInLineImpl(moved, BallColor.YELLOW);
			LinkedList<Coord> listB = getBallsInLineImpl(moved, BallColor.BLUE);
			LinkedList<Coord> listO = getBallsInLineImpl(moved, BallColor.ORANGE);
			LinkedList<Coord> listC = getBallsInLineImpl(moved, BallColor.CELESTE);
			LinkedList<Coord> listG = getBallsInLineImpl(moved, BallColor.GREEN);
			LinkedList<Coord> listV = getBallsInLineImpl(moved, BallColor.VIOLET);

			coordsInLine = new LinkedList<Coord>();

			listR.removeAll(coordsInLine); coordsInLine.addAll(listR);
			listY.removeAll(coordsInLine); coordsInLine.addAll(listY);
			listB.removeAll(coordsInLine); coordsInLine.addAll(listB);
			listO.removeAll(coordsInLine); coordsInLine.addAll(listO);
			listC.removeAll(coordsInLine); coordsInLine.addAll(listC);
			listG.removeAll(coordsInLine); coordsInLine.addAll(listG);
			listV.removeAll(coordsInLine); coordsInLine.addAll(listV);
		}


		return coordsInLine;
	}

	private LinkedList<Coord> getBallsInLineImpl(Coord moved, BallColor ballColor) throws EmptyCellExeption {
		// Lista que contendra todas las bolas a borrar del tablero porque se
		// encuentran en linea.
		LinkedList<Coord> coordsInLine = new LinkedList<Coord>();

		// Busco las coordenadas que estan en linea en la direccion vertical.
		LinkedList<Coord> verticalInLine = getInLine(moved, Direction.SOUTH,
				ballColor);
		if (!verticalInLine.isEmpty()) {
			verticalInLine.removeAll(coordsInLine);
			coordsInLine.addAll(verticalInLine);
		}

		// Busco las coordenadas que estan en linea en la direccion de la
		// diagonal principal.
		LinkedList<Coord> mainDiagonalInLine = getInLine(moved,
				Direction.SOUTHEAST, ballColor);
		if (!mainDiagonalInLine.isEmpty()) {
			mainDiagonalInLine.removeAll(coordsInLine);
			coordsInLine.addAll(mainDiagonalInLine);
		}

		// Busco las coordenadas que estan en linea en la direccion de la
		// diagonal secundaria.
		LinkedList<Coord> secondaryDiagonalInLine = getInLine(moved,
				Direction.SOUTHWEST, ballColor);
		if (!secondaryDiagonalInLine.isEmpty()) {
			secondaryDiagonalInLine.removeAll(coordsInLine);
			coordsInLine.addAll(secondaryDiagonalInLine);
		}

		// Busco las coordenadas que estan en linea en la direccion horizontal.
		LinkedList<Coord> horizontalInLine = getInLine(moved, Direction.EAST,
				ballColor);
		if (!horizontalInLine.isEmpty()) {
			horizontalInLine.removeAll(coordsInLine);
			coordsInLine.addAll(horizontalInLine);
		}

		return coordsInLine;
	}

	/**
	 * 
	 * @param coord
	 * @return Devuelve la celda de la posicion <code>coord</code>
	 */
	private Cell getCell(Coord coord) {
		return getCell(coord.getX(), coord.getY());
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return Devuelve la celda que se encuentra en la posicion <code>x</code>
	 *         e <code>y</code>.
	 */
	private Cell getCell(int x, int y) {
		return this.cells[x][y];
	}

	/**
	 * Devuelve la cantidad de celdas libres.
	 * 
	 * @return
	 */
	public int getCountFree() {
		return this.freeCells.size();
	}

	/**
	 * 
	 * @param moved
	 * @param dir
	 *            Especifica el comienzo del recorrido. Luego se reccore en la
	 *            direccion inversa.
	 * @param ballColor
	 * @return Devuelve las coordenadas que se encuentra en cinco en linea ya
	 *         sea en posicion vertical, diagonal, diagonal inversa o
	 *         horizontal.
	 */
	private LinkedList<Coord> getInLine(Coord moved, Direction dir,
			BallColor ballColor) {

		// Lista de coordenadas.
		LinkedList<Coord> inLine = new LinkedList<Coord>();

		// Obtengo la coordenada mas proxima de moved, en la direccion dir.
		Coord dirNextCoord = moved.getNextCoord(dir);

		// Si existe tal coordenada, busco en esa direccion las que estan en
		// linea con el mismo color.
		if (dirNextCoord != null) {
			inLine = inDir(dirNextCoord, dir, ballColor);
		}

		// Siempre se agrega porque la celda de la coordenada moved, contine una
		// bola del color ballColor
		inLine.add(moved);

		// Tomo la direccion opuesta para recorren en el sentido contrario.
		Direction oposite = Direction.getOposite(dir);

		// Obtengo la coordenada mas proxima a moved, en la direccion contraria
		// a dir.
		Coord opositeNextCoord = moved.getNextCoord(oposite);

		// Si existe tal coordenada, busco en la direccion opuestaa las que
		// estan en linea con el mismo color.
		if (opositeNextCoord != null) {
			inLine.addAll(inDir(opositeNextCoord, oposite, ballColor));
		}

		// Si encontre una de cooredenadas menor a la minima, devuelvo una lista
		// vacia
		if (inLine.size() < Consts.game.inlinemin) {
			inLine.clear();
		}

		return inLine;
	}

	/**
	 * Retorna el path encontrado con el metodo hasPathTo.
	 * 
	 * @return
	 */
	public LinkedList<Coord> getPath() {
		return path;
	}

	/**
	 * 
	 * @return devuelve un ballColor aleatorio.
	 */
	public BallColor getRandomBallColor() {
		int color = nextColorVal();
		BallColor ballColor = BallColor.getBallColor(color);

		return ballColor;
	}

	/**
	 * 
	 * @return devuelve una celda libre aleatoriamente.
	 * Throw exception si existe una celda libre.
	 */
	public Coord getRandomCoord() throws NotExistEmptyCellException {

		int size = freeCells.size();
		if (size == 0) {
			throw new NotExistEmptyCellException();
		}

		// Busco entreo aleatorio en el rango 0..size-1
		int posCoord = nextVal(size);
		// Obtengo la celda
		Coord coord = freeCells.get(posCoord);

		return coord;
	}

	/**
	 * Devuelve la coordenada de la celda seleccionada.
	 * 
	 * @return
	 */
	public Coord getSelected() {
		return selected;
	}

	/**
	 * 
	 * @return Devuelve el color de la bola seleccionada
	 * @throws UnSelectedBallExpetion
	 *             Si no existe un bola seleccionada
	 * @throws EmptyCellExeption
	 *             Si la celda de la posicion seleccionada se encuentra vacia.
	 */
	public BallColor getSelectedColor() throws UnSelectedBallExpetion,
			EmptyCellExeption {
		if (!hasSelected()) {
			throw new UnSelectedBallExpetion();
		}

		// Tomo la bola seleccionada
		Ball ball = getBall(selected);

		// Retorno su color
		return ball.getBallColor();
	}

	/**
	 * Checkea si la celda que se encuentra en la coordenada <code>coord</code>
	 * contiene una bola.
	 * 
	 * @param coord
	 *            Coordenada de la celda.
	 * @return Retorna <code>true</code> si la celda de la posicion
	 *         <code>coord</code> contiene una bola.
	 */
	public boolean hasBall(Coord coord) {
		Cell cell = getCell(coord);
		if (cell.getBall() != null) {
			return true;
		}
		return false;
	}

	private boolean hasPath(Coord init, Coord end, int[][] table) {

		LinkedList<Coord> coords = new LinkedList<Coord>();
		coords.add(init);

		boolean found = false;
		int count = 0;

		while (!coords.isEmpty() && !found) {

			LinkedList<Coord> coordsAux = new LinkedList<Coord>();
			coordsAux.addAll(coords);

			coords = new LinkedList<Coord>();

			for (Coord coord : coordsAux) {
				int x = coord.getX();
				int y = coord.getY();
				if (table[x][y] < 0 || table[x][y] > count) {
					table[x][y] = count;
					LinkedList<Coord> nearCoords = getAvialbleNearCoords(coord,
							end, table);

					nearCoords.removeAll(coords);
					coords.addAll(nearCoords);
				}

				if (coord.equals(end)) {
					found = true;
				}
			}
			count++;
		}

		return found;
	}

	/**
	 * Busca un camino entre la celda seleccionada y la celda indicada como
	 * destino
	 * 
	 * @param end
	 *            Coordenada que inidca el fin del camino.
	 *            En caso que se devuelva true, se guarda el camino hallado.
	 * @return Devuelve true si existe un camino entre la coordenada
	 *         seleccionada y el fin indicado.
	 */
	public boolean hasPathTo(Coord end) {
		this.path.clear();
		// LinkedList<Coord> visited = new LinkedList<Coord>();

		int table[][] = new int[Consts.game.gridsize][Consts.game.gridsize];
		for (int i = 0; i < Consts.game.gridsize; i++) {
			for (int j = 0; j < Consts.game.gridsize; j++) {
				table[i][j] = 100;
			}
		}

		if (hasPath(selected, end, table)) {

			LinkedList<Coord> visited = new LinkedList<Coord>();
			visited.add(end);
			Coord aux = new Coord(end);
			while (!aux.equals(selected)) {
				this.path.addFirst(aux);
				LinkedList<Coord> nearCoords = aux.nearCoordsSimple();
				nearCoords.removeAll(visited);
				visited.addAll(nearCoords);

				int min = Integer.MAX_VALUE;
				for (Coord nearCoord : nearCoords) {
					int x = nearCoord.getX();
					int y = nearCoord.getY();
					if (table[x][y] < min) {
						min = table[x][y];
						aux = nearCoord;
					}
				}
			}
			this.path.addFirst(selected);

			return true;
		} else {
			return false;
		}

	}

	/**
	 * Chekea si hay una celda seleccionada.
	 * 
	 * @return Devuelve true si existe una celda seleccionada.
	 */
	public boolean hasSelected() {
		if (selected != null) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param coord
	 * @param dir
	 * @param ballColor
	 * @return Devuelve las coordenadas de las celdas que contiene una bola de
	 *         color ballColor y que estan en linea en la direccion dir
	 */
	private LinkedList<Coord> inDir(Coord coord, Direction dir,
			BallColor ballColor) {

		LinkedList<Coord> result = new LinkedList<Coord>();

		Cell cell = getCell(coord);
		if (cell.isFree()) {
			return result;
		}

		Ball ball = cell.getBall();
		if (ball.getBallColor() != ballColor && (ballColor != BallColor.WILDCARD && ball.getBallColor() != BallColor.WILDCARD)) {
			return result;
		}

		result.add(coord);

		// Tomo la coordenada mas proxima a coord en la direccion dir
		Coord nextCoord = coord.getNextCoord(dir);
		if (nextCoord != null) { // No me fui afuera del tablero
			// Agrego el resultado de buscar en linea las bolas de color
			// ballColor en la direccion dir
			result.addAll(inDir(nextCoord, dir, ballColor));
		}

		return result;
	}

	/**
	 * Mueve la bola que se encuentra en la coordenada seleccionada a la
	 * coordenada <code>coord</code>.
	 * 
	 * @param coord
	 *            Coordenada a la que se quiere mover la bola seleccionada.
	 * @throws UnSelectedBallExpetion
	 *             Si no existe una bola seleccionada.
	 * @throws EmptyCellExeption
	 *             Si la celda de la bola seleccionada es no contine alguna
	 *             bola.
	 * @throws NotEmptyCellExeption
	 *             Si la celda destino esta ocupada por otra bola.
	 */
	public void moveTo(Coord coord) throws UnSelectedBallExpetion,
			EmptyCellExeption, NotEmptyCellExeption {
		if (!hasSelected()) {
			throw new UnSelectedBallExpetion();
		}

		Cell init = getCell(selected);
		if (init.isFree()) {
			throw new EmptyCellExeption(selected);
		}

		Cell end = getCell(coord);
		if (!end.isFree()) {
			throw new NotEmptyCellExeption(coord);
		}

		// Tomo la ball para moverla
		Ball ball = init.getBall();

		// La saco de la celda inicial y la marco como libre
		init.setBall(null);
		freeCells.add(selected);

		// Pongo la bola en la celda destino y la marco como no libre
		end.setBall(ball);
		freeCells.remove(coord);

		// Se deselecciona la bola de la celda seleccionada
		selected = null;
	}

	/**
	 * 
	 * @return Devuelve un entreo aleatorio dentro del rango 0..7
	 */
	private int nextColorVal() {
		int nextInt = randomColor.nextInt(8);
		return nextInt;
	}

	/**
	 * 
	 * @param range
	 * @return Devuelve un entreo aleatorio dentro del rango 0..range-1
	 */
	private int nextVal(int range) {
		int nextInt = randomCell.nextInt(range);
		return nextInt;
	}

	/**
	 * Borra del tablero las bolas que estan en las coordeandas
	 * <code>toRemoveBalls</code>
	 * 
	 * @param toRemoveBalls
	 * @throws EmptyCellExeption
	 *             Si alguna de las celdas de <code>toRemoveBalls</code> esta
	 *             libre.
	 */
	public void removeBalls(LinkedList<Coord> toRemoveBalls)
			throws EmptyCellExeption {

		for (Coord coord : toRemoveBalls) {
			Cell cell = getCell(coord);
			if (cell.isFree()) {
				throw new EmptyCellExeption(coord);
			}
			// Borro la bola de la celda.
			cell.setBall(null);

			// Agrego la coordenada como libre
			freeCells.add(coord);
		}
	}

	/**
	 * Se selecciona la ball que se encuentra en la celda inidicada por coord.
	 * 
	 * @param coord
	 *            Coordenada de la celda que contiene la bola a seleccionar.
	 * @return Retora el color de la bola seleccionada.
	 * @throws EmptyCellExeption
	 *             Si la celda es vacia
	 */
	public BallColor selectBall(Coord coord) throws EmptyCellExeption {
		Cell cell = getCell(coord);
		if (cell.isFree()) {
			throw new EmptyCellExeption(coord);
		}

		this.selected = new Coord(coord);
		return cell.getBall().getBallColor();
	}

	/**
	 * Setea el path
	 * 
	 * @param path
	 */
	private void setPath(LinkedList<Coord> path) {
		this.path = path;
	}

	/**
	 * Deselecciona la coordenada seleccionada.
	 * 
	 * @throws UnSelectedBallExpetion
	 *             Si no existe bola seleccionada.
	 */
	public void unselect() throws UnSelectedBallExpetion {
		if (!hasSelected()) {
			throw new UnSelectedBallExpetion();
		}
		this.selected = null;
	}

	public LinkedList<Coord> getFreeCells() {
		return this.freeCells;
	}

	public boolean emptyGrid() {
		return freeCells.size() == (Consts.game.gridsize * Consts.game.gridsize);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("    0     1     2     3     4     5     6     7     8   \n");
		builder.append("  _____________________________________________________\n");

		for (int i = 0; i < Consts.game.gridsize; i++) {
			builder
					.append(" |     |     |     |     |     |     |     |     |     |\n");
			builder.append(i);
			builder.append("|");
			for (int j = 0; j < Consts.game.gridsize; j++) {
				builder.append("  ");
				Cell cell = getCell(i, j);
				if (cell.getBall() == null) {
					builder.append(" ");
				} else {
					Ball ball = cell.getBall();
					char c = BallColor.getBallColorChar(ball.getBallColor());
					builder.append(c);
				}
				builder.append("  |");
			}
			builder.append("\n");
			builder
					.append(" |_____|_____|_____|_____|_____|_____|_____|_____|_____|\n");
		}
		builder.append("\n");
		return builder.toString();
	}

	public void log() {

		StringBuilder builder = new StringBuilder();
		builder.append("  0 1 2 3 4 5 6 7 8");
		Log.i(TAG, builder.toString());
		builder = new StringBuilder();

		for (int i = 0; i < Consts.game.gridsize; i++) {
			builder.append(i);
			for (int j = 0; j < Consts.game.gridsize; j++) {
				Cell cell = getCell(i, j);
				if (cell.getBall() == null) {
					builder.append("  ");
				} else {
					Ball ball = cell.getBall();
					char c = BallColor.getBallColorChar(ball.getBallColor());
					builder.append(" " + c);
				}
			}
			Log.i(TAG, builder.toString());
			builder = new StringBuilder();
		}
		Log.i(TAG, "");
	}

}
