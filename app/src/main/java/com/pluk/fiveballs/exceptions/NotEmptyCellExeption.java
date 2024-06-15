package com.pluk.fiveballs.exceptions;

import com.pluk.fiveballs.model.Coord;

public class NotEmptyCellExeption extends Exception {

	private static final long serialVersionUID = -380964956642890833L;

	public NotEmptyCellExeption(Coord coord) {
		super("cell " + coord.toString() + " is not free");
	}
}
