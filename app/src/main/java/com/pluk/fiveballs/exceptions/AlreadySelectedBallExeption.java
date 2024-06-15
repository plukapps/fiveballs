package com.pluk.fiveballs.exceptions;

import com.pluk.fiveballs.model.Coord;

public class AlreadySelectedBallExeption extends Exception {

	private static final long serialVersionUID = 6760516867997291094L;

	public AlreadySelectedBallExeption(Coord selected) {
		super("Already selected a ball: " + selected);
	}

}
