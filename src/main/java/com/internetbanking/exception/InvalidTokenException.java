package com.internetbanking.exception;

public class InvalidTokenException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidTokenException() {
		
	}
	public InvalidTokenException(String message) {
		super(message);
		
	}

}
