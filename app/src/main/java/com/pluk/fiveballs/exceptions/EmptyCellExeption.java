package com.pluk.fiveballs.exceptions;

import com.pluk.fiveballs.model.Coord;

public class EmptyCellExeption extends Exception {

	private static final long serialVersionUID = 6684555972847430007L;

	public EmptyCellExeption(Coord coord) {
		super("cell " + coord.toString() + " is free");
	}
}
