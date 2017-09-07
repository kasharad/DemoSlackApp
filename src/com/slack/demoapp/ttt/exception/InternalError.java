package com.slack.demoapp.ttt.exception;

public class InternalError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3521415371591323437L;

	public InternalError(String string) {
		super(string);
	}

	public InternalError(Exception e) {
		super(e);
	}

}
