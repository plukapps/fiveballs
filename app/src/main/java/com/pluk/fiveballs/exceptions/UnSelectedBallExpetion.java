package com.pluk.fiveballs.exceptions;

public class UnSelectedBallExpetion extends Exception {

	private static final long serialVersionUID = 5385685352624817026L;

	public UnSelectedBallExpetion() {
		super("Not exist selected ball");
	}

}
