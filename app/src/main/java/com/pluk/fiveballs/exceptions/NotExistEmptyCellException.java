package com.pluk.fiveballs.exceptions;

public class NotExistEmptyCellException extends Exception {

	private static final long serialVersionUID = 4730897255450829280L;

	public NotExistEmptyCellException() {
		super("No exist a free cell");
	}

}
