package com.pluk.fiveballs.handlers;

import java.util.HashMap;
import java.util.LinkedList;

import com.pluk.fiveballs.exceptions.AlreadySelectedBallExeption;
import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotEmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotExistEmptyCellException;
import com.pluk.fiveballs.exceptions.UnSelectedBallExpetion;
import com.pluk.fiveballs.model.BallColor;
import com.pluk.fiveballs.model.Coord;
import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.model.BallColor;
import com.pluk.fiveballs.model.Coord;

public interface IGameManager {

	/**
	 * Agrega aleatoriamente las nuevas bolas con los colores inidcados
	 * @param balls
	 * @return Devuelve las coordenadas donde las bolas fueron insertadas.
	 * @throws FiveMoreExeption 
	 * @throws NotEmptyCellExeption Si alguna de las celdas elegidas para insertarse no esta vacia.
	 * @throws EmptyCellExeption Si despues de agregadas la celdas alguna quedo vacia.
	 * @throws NotExistEmptyCellException Si no hay mas celdas vacias.
	 */
	public HashMap<Coord, BallColor> addBalls(LinkedList<BallColor> balls) throws NotEmptyCellExeption, EmptyCellExeption, NotExistEmptyCellException;
	
	/**
	 * 
	 * @return Devuelve los colores de las proximas bolas.
	 */
	public LinkedList<BallColor> getNextColorBalls();
	
	public String getPanelGrid();
	
	/**
	 * 
	 * @return Devuelve el scroe
	 */
	public int getScore();
	
	/**
	 * 
	 * @return Devuelve la bola seleccionada
	 */
	public Coord getSelected();	

	/**
	 * Devuelve el color de la bola seleccionda.
	 * @return
	 * @throws UnSelectedBallExpetion
	 * @throws EmptyCellExeption
	 */
	public BallColor getSelectedColor() throws UnSelectedBallExpetion, EmptyCellExeption;
	
	/**
	 * 
	 * @return Devuelve true si existen cinco o mas en linea.
	 */
	public boolean hasBallsInLine();
	
	/**
	 * Devuelve true si existe una bola seleccionada.
	 * @return
	 */
	public boolean hasSelected();

	/**
	 * Mueve la bola seleccionada a la posicion indicada por <code>coord</code>
	 * @param coord
	 * @return Retorna el camino recorrido en la movida
	 * @throws UnSelectedBallExpetion Si no existe una bola seleccionada
	 * @throws EmptyCellExeption Si la bola selleccionada esta vacia
	 * @throws NotEmptyCellExeption Si la celda detsino esta ocupada.
	 * @throws NotFoundPathException Si no se encontro un camino.
	 */
	public LinkedList<Coord> moveTo(Coord coord) throws UnSelectedBallExpetion, EmptyCellExeption, NotEmptyCellExeption;

	/**
	 * Borra las bolas que se encuentra en cinco o mas en linea
	 * @throws EmptyCellExeption
	 */
	public void removeBalls() throws EmptyCellExeption;

	/**
	 * Selecciona la bola que se encuentra en la posicion <code>coord</code>
	 * @param coord
	 * @return Retorna el color de la bola
	 * @throws AlreadySelectedBallExeption Si ya existe una bola seleccionada
	 * @throws EmptyCellExeption Si la celda que esta en la coordenada <code>coord</code>
	 */
	public BallColor select(Coord coord) throws AlreadySelectedBallExeption, EmptyCellExeption;

	/**
	 * Deselecciona la bola seleccionada.
	 * @throws UnSelectedBallExpetion Si no exite una bola seleccionda.
	 */
	public void unselect() throws UnSelectedBallExpetion;

	/**
	 * Devuelve true si hay una bola en la corrdenada <code>imageCoord</code>.
	 * @param imageCoord Coordenada en la que se busca una imagen 
	 * @return
	 */
	public boolean hasBall(Coord imageCoord);
	
	/**
	 * Devuelve la lista de Coordenadas que se encuentran libres.
	 * @return
	 */
	public LinkedList<Coord> freeCells();

	/**
	 * Devuelve las actuales bolas siguientes
	 * @return
	 */
	public LinkedList<BallColor> getCurrentNextBalls();
	
	/**
	 * Devuelve las bolas que estan en 5 o mas
	 * @return
	 */
	public LinkedList<Coord> getCoordsInLine();

	public boolean emptyGrid();

	BallColor getBallColor(Coord coord) throws EmptyCellExeption;
	
}
